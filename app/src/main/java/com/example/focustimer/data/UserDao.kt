package com.example.focustimer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)
}
