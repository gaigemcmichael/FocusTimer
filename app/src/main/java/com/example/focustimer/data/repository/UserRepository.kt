package com.example.focustimer.data.repository

import com.example.focustimer.data.dao.UserDao
import com.example.focustimer.data.model.User

class UserRepository(private val userDao: UserDao) {
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }
}