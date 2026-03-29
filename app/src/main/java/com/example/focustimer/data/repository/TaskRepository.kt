package com.example.focustimer.data.repository

import androidx.lifecycle.LiveData
import com.example.focustimer.data.dao.TaskDao
import com.example.focustimer.data.model.Task

class TaskRepository(private val taskDao: TaskDao) {

    fun getIncompleteTasksLiveData(userId: String): LiveData<List<Task>> {
        return taskDao.getIncompleteTasksLiveData(userId)
    }

    suspend fun getIncompleteTasks(userId: String): List<Task> {
        return taskDao.getIncompleteTasks(userId)
    }

    fun getCompletedTasksLiveData(userId: String): LiveData<List<Task>> {
        return taskDao.getCompletedTasksLiveData(userId)
    }

    suspend fun insert(task: Task): Long {
        return taskDao.insertTask(task)
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

    suspend fun deleteTasksByUserId(userId: String) {
        taskDao.deleteTasksByUserId(userId)
    }
}
