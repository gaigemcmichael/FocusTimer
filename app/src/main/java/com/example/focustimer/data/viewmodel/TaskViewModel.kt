package com.example.focustimer.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.focustimer.data.FocusTimerApplication
import com.example.focustimer.data.model.Task
import com.example.focustimer.data.model.TaskStatus
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "TaskViewModel"

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as FocusTimerApplication
    private val repository = app.taskRepository
    private val tasksService = app.googleTasksService

    private val _currentUserId = MutableLiveData<String?>()
    private val pushingTasks = ConcurrentHashMap.newKeySet<Int>()
    private val updatingTasks = ConcurrentHashMap.newKeySet<Int>()
    private val deletingGoogleIds = ConcurrentHashMap.newKeySet<String>()

    val incompleteTasks: LiveData<List<Task>> = _currentUserId.switchMap { userId ->
        if (userId == null) MutableLiveData(emptyList())
        else repository.getIncompleteTasksLiveData(userId)
    }

    val completedTasks: LiveData<List<Task>> = _currentUserId.switchMap { userId ->
        if (userId == null) MutableLiveData(emptyList())
        else repository.getCompletedTasksLiveData(userId)
    }



    private val _taskIdToUpdate = MutableLiveData<Int>()
    val taskToUpdate: LiveData<Task?> = _taskIdToUpdate.switchMap { id ->
        repository.getTaskByIdLiveData(id)
    }

    fun setCurrentUser(userId: String?) {
        _currentUserId.value = userId
    }

    fun addTask(title: String, description: String, dueDate: Date, googleAccount: String? = null) {
        val userId = _currentUserId.value ?: return
        viewModelScope.launch {
            val newTask = Task(
                userId = userId,
                title = title,
                description = description,
                createDate = Date(),
                dueDate = dueDate,
                status = TaskStatus.NEW
            )
            
            val taskId = repository.insert(newTask).toInt()
            
            if (googleAccount != null) {
                pushTaskToGoogleSafely(googleAccount, newTask.copy(id = taskId))
            }
        }
    }

    private fun pushTaskToGoogleSafely(googleAccount: String, task: Task) {
        if (task.id == 0 || task.googleEventId != null) return
        if (!pushingTasks.add(task.id)) return // Already being pushed

        viewModelScope.launch {
            try {
                val remoteId = tasksService.pushTaskToGoogle(googleAccount, task)
                if (remoteId != null) {
                    repository.update(task.copy(googleEventId = remoteId))
                }
            } finally {
                pushingTasks.remove(task.id)
            }
        }
    }

    fun syncWithGoogleTasks(googleAccount: String) {
        val userId = _currentUserId.value ?: return
        Log.d(TAG, "Syncing with Google Tasks for: $googleAccount")
        viewModelScope.launch {
            try {
                // Pull tasks from Google
                val remoteTasks = tasksService.fetchGoogleTasks(googleAccount, userId)
                remoteTasks.forEach { remoteTask ->
                    val googleId = remoteTask.googleEventId
                    // Safety check: Don't re-insert tasks that are currently being deleted
                    if (googleId != null && !deletingGoogleIds.contains(googleId)) {
                        val existingLocalTask = repository.getTaskByGoogleId(googleId)
                        if (existingLocalTask != null) {
                            // Don't overwrite tasks that are currently being updated or pushed
                            if (!updatingTasks.contains(existingLocalTask.id) && !pushingTasks.contains(existingLocalTask.id)) {
                                // Preserving local ID so UI/ViewModels holding references don't break
                                repository.update(remoteTask.copy(id = existingLocalTask.id))
                            }
                        } else {
                            repository.insert(remoteTask)
                        }
                    }
                }

                // Push existing tasks in Room db without remoteId to calendar
                val localTasks = repository.getIncompleteTasks(userId)
                localTasks.forEach { task ->
                    if (task.googleEventId == null) {
                        val alreadyOnGoogle = remoteTasks.any {
                            it.title.equals(task.title, ignoreCase = true)
                        }
                        if (!alreadyOnGoogle) {
                            pushTaskToGoogleSafely(googleAccount, task)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sync failed", e)
            }
        }
    }

    fun loadTaskById(taskId: Int) {
        _taskIdToUpdate.value = taskId
    }

    fun updateTask(task: Task, googleAccount: String? = null) {
        if (!updatingTasks.add(task.id)) return // Already being updated

        viewModelScope.launch {
            try {
                repository.update(task)
                if (googleAccount != null && task.googleEventId != null) {
                    tasksService.updateGoogleTask(googleAccount, task)
                }
            } finally {
                updatingTasks.remove(task.id)
            }
        }
    }

    fun deleteTask(task: Task, googleAccount: String? = null) {
        val googleId = task.googleEventId
        if (googleId != null) {
            deletingGoogleIds.add(googleId)
        }
        
        viewModelScope.launch {
            try {
                // 1. Delete by primary key
                repository.delete(task)
                
                // 2. Extra safety: If it has a Google ID, ensure it's gone locally 
                // even if the passed 'task' had a stale primary key ID.
                if (googleId != null) {
                    val localTask = repository.getTaskByGoogleId(googleId)
                    if (localTask != null) {
                        repository.delete(localTask)
                    }
                    
                    // 3. Delete from Google
                    if (googleAccount != null) {
                        tasksService.deleteGoogleTask(googleAccount, googleId)
                    }
                }
            } finally {
                if (googleId != null) {
                    deletingGoogleIds.remove(googleId)
                }
            }
        }
    }


}
