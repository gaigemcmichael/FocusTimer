package com.example.focustimer.data

import android.app.Application
import com.example.focustimer.data.repository.UserRepository

class FocusTimerApplication : Application() {
    val database by lazy { FocusTimerDatabase.getDatabase(this) }
    val repository by lazy { UserRepository(database.userDao()) }
}
