package com.example.focustimer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.focustimer.data.dao.TimerDao
import com.example.focustimer.data.model.FocusMethod
import com.example.focustimer.data.model.FocusSession
import kotlinx.coroutines.flow.Flow

class TimerRepository(private val timerDao: TimerDao) {
    suspend fun insertSession(session: FocusSession) {
        timerDao.insertSession(session)
    }

    suspend fun insertSessions(sessions: List<FocusSession>) {
        timerDao.insertSessions(sessions)
    }

    fun getSessionsPaging(username: String): Flow<PagingData<FocusSession>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20,
                maxSize = 60 // Keeps memory usage low by dropping old pages
            ),
            pagingSourceFactory = { timerDao.getSessionsPaging(username) }
        ).flow
    }

    suspend fun getSessionsByUsername(username: String): List<FocusSession> {
        return timerDao.getSessionsByUsername(username)
    }

    suspend fun insertFocusMethod(method: FocusMethod) {
        timerDao.insertFocusMethod(method)
    }

    suspend fun getAllFocusMethods(): List<FocusMethod> {
        return timerDao.getAllFocusMethods()
    }

    suspend fun prepopulateMethods() {
        insertFocusMethod(FocusMethod("POMODORO", "25min focus - 5min break", 25, 5))
        insertFocusMethod(FocusMethod("CLASSIC", "Set your own timer", 0, 0))
        insertFocusMethod(FocusMethod("FLOWMODORO", "Focus session, then 1/5 break", 0, 0))
    }
}
