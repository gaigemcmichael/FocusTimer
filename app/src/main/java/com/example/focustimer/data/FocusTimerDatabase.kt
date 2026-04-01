package com.example.focustimer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.focustimer.data.dao.TaskDao
import com.example.focustimer.data.dao.TimerDao
import com.example.focustimer.data.dao.UserDao
import com.example.focustimer.data.model.FocusMethod
import com.example.focustimer.data.model.FocusSession
import com.example.focustimer.data.model.Task
import com.example.focustimer.data.model.User

@Database(entities = [User::class, Task::class, FocusSession::class, FocusMethod::class], version = 12, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FocusTimerDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun timerDao(): TimerDao

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
                            .addCallback(DatabaseCallback())
                            .setJournalMode(JournalMode.TRUNCATE) // Helps inspector stay connected
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return sInstance!!
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Pre-populate focus methods only once when DB is first created
                db.execSQL("INSERT INTO focus_methods (focusMethodId, description, focusDuration, breakDuration) VALUES ('POMODORO', '25min focus - 5min break', 25, 5)")
                db.execSQL("INSERT INTO focus_methods (focusMethodId, description, focusDuration, breakDuration) VALUES ('CLASSIC', 'Set your own timer', 0, 0)")
                db.execSQL("INSERT INTO focus_methods (focusMethodId, description, focusDuration, breakDuration) VALUES ('FLOWMODORO', 'Focus session, then 1/5 break', 0, 0)")
            }
        }
    }
}
