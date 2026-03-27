package com.example.focustimer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.focustimer.data.dao.TaskDao
import com.example.focustimer.data.dao.UserDao
import com.example.focustimer.data.model.Task
import com.example.focustimer.data.model.User

@Database(entities = [User::class, Task::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FocusTimerDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var sInstance: FocusTimerDatabase? = null

        fun getDatabase(context: Context): FocusTimerDatabase {
            if (sInstance == null) {
                synchronized(FocusTimerDatabase::class.java) {
                    if (sInstance == null) {
                        sInstance = Room.databaseBuilder(
                            context.applicationContext,
                            FocusTimerDatabase::class.java, "focus_timer_database"
                        )
                            .build()
                    }
                }
            }
            return sInstance!!
        }
    }
}
