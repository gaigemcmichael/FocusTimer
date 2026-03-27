package com.example.focustimer.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.focustimer.data.FocusTimerApplication
import com.example.focustimer.data.model.Task
import com.example.focustimer.data.model.TaskStatus
import kotlinx.coroutines.launch
import java.util.Date

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as FocusTimerApplication).taskRepository

    val incompleteTasks: LiveData<List<Task>> = repository.incompleteTasks
    val completedTasks: LiveData<List<Task>> = repository.completedTasks

    private val _taskToUpdate = MutableLiveData<Task?>()
    val taskToUpdate: LiveData<Task?> get() = _taskToUpdate

    fun addTask(title: String, description: String, dueDate: Date) {
        viewModelScope.launch {
            val newTask = Task(
                title = title,
                description = description,
                createDate = Date(),
                dueDate = dueDate,
                status = TaskStatus.NEW
            )
            repository.insert(newTask)
        }
    }

    fun loadTaskById(taskId: Int) {
        viewModelScope.launch {
            _taskToUpdate.value = repository.getTaskById(taskId)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.update(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }
}
