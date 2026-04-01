package com.example.focustimer.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.focustimer.data.model.FocusMethod
import com.example.focustimer.data.model.FocusSession

@Dao
interface TimerDao {
    @Insert
    suspend fun insertSession(session: FocusSession)

    @Query("SELECT * FROM focus_sessions WHERE userName = :username")
    suspend fun getSessionsByUsername(username: String): List<FocusSession>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFocusMethod(method: FocusMethod)

    @Query("SELECT * FROM focus_methods")
    suspend fun getAllFocusMethods(): List<FocusMethod>
}
