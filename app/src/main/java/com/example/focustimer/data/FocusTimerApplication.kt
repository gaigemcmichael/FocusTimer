package com.example.focustimer.data

import android.app.Application
import com.example.focustimer.data.repository.TaskRepository
import com.example.focustimer.data.repository.UserRepository

class FocusTimerApplication : Application() {
    val database by lazy { FocusTimerDatabase.getDatabase(this) }
    val repository by lazy { UserRepository(database.userDao()) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }
    val googleTasksService by lazy { GoogleTasksService(this) }
}
