package com.example.focustimer.data

import android.app.Application
import com.example.focustimer.data.repository.TaskRepository
import com.example.focustimer.data.repository.TimerRepository
import com.example.focustimer.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FocusTimerApplication : Application() {
    val database by lazy { FocusTimerDatabase.getDatabase(this) }
    val repository by lazy { UserRepository(database.userDao()) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }
    val timerRepository by lazy { TimerRepository(database.timerDao()) }
    val googleTasksService by lazy { GoogleTasksService(this) }

    override fun onCreate() {
        super.onCreate()
        // Pre-populate focus methods on app startup to ensure they exist
        CoroutineScope(Dispatchers.IO).launch {
            timerRepository.prepopulateMethods()
        }
    }
}
