package com.example.focustimer.data.repository

import androidx.lifecycle.LiveData
import com.example.focustimer.data.dao.TaskDao
import com.example.focustimer.data.model.Task

class TaskRepository(private val taskDao: TaskDao) {

    val incompleteTasks: LiveData<List<Task>> = taskDao.getIncompleteTasks()
    val completedTasks: LiveData<List<Task>> = taskDao.getCompletedTasks()

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)
    }
}
