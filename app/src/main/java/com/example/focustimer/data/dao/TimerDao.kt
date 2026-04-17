package com.example.focustimer.data.dao

import androidx.paging.PagingSource
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<FocusSession>)

    @Query("SELECT * FROM focus_sessions WHERE userName = :username ORDER BY startTime DESC")
    fun getSessionsPaging(username: String): PagingSource<Int, FocusSession>

    @Query("SELECT * FROM focus_sessions WHERE userName = :username")
    suspend fun getSessionsByUsername(username: String): List<FocusSession>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFocusMethod(method: FocusMethod)

    @Query("SELECT * FROM focus_methods")
    suspend fun getAllFocusMethods(): List<FocusMethod>
}
