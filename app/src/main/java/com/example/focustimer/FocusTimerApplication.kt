package com.example.focustimer

import android.app.Application
import com.example.focustimer.data.FocusTimerDatabase
import com.example.focustimer.data.UserRepository

class FocusTimerApplication : Application() {
    val database by lazy { FocusTimerDatabase.getDatabase(this) }
    val repository by lazy { UserRepository(database.userDao()) }
}
