package com.example.focustimer.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.focustimer.data.FocusTimerApplication
import com.example.focustimer.data.model.Task
import com.example.focustimer.data.model.TaskStatus
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "TaskViewModel"

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as FocusTimerApplication
    private val repository = app.taskRepository
    private val tasksService = app.googleTasksService

    private val _currentUserId = MutableLiveData<String?>()
    private val pushingTasks = ConcurrentHashMap.newKeySet<Int>()

    val incompleteTasks: LiveData<List<Task>> = _currentUserId.switchMap { userId ->
        if (userId == null) MutableLiveData(emptyList())
        else repository.getIncompleteTasksLiveData(userId)
    }

    val completedTasks: LiveData<List<Task>> = _currentUserId.switchMap { userId ->
        if (userId == null) MutableLiveData(emptyList())
        else repository.getCompletedTasksLiveData(userId)
    }

    val tasksDueNextWeek: LiveData<Int> = incompleteTasks.map { list ->
        val calendar = Calendar.getInstance()
        val now = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val nextWeek = calendar.time
        list.count { it.dueDate in now..nextWeek }
    }

    private val _taskToUpdate = MutableLiveData<Task?>()
    val taskToUpdate: LiveData<Task?> get() = _taskToUpdate

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
                // Pull tasks from Calendar
                // Pass the userId to fetchGoogleTasks so it can correctly construct Task objects
                val remoteTasks = tasksService.fetchGoogleTasks(googleAccount, userId)
                remoteTasks.forEach { task ->
                    repository.insert(task)
                }

                // Push existing tasks in Room db without remoteId to calendar
                val localTasks = repository.getIncompleteTasks(userId)
                localTasks.forEach { task ->
                    if (task.googleEventId == null) {
                        // Check if this task title already exists in the remoteTasks we just fetched
                        // to prevent creating duplicates on Calendar
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
        viewModelScope.launch {
            _taskToUpdate.value = repository.getTaskById(taskId)
        }
    }

    fun updateTask(task: Task, googleAccount: String? = null) {
        viewModelScope.launch {
            repository.update(task)
            if (googleAccount != null && task.googleEventId != null) {
                tasksService.updateGoogleTask(googleAccount, task)
            }
        }
    }

    fun deleteTask(task: Task, googleAccount: String? = null) {
        viewModelScope.launch {
            repository.delete(task)
            if (googleAccount != null && task.googleEventId != null) {
                tasksService.deleteGoogleTask(googleAccount, task.googleEventId)
            }
        }
    }

    fun deleteUserTasks(userId: String) {
        viewModelScope.launch {
            repository.deleteTasksByUserId(userId)
        }
    }
}
