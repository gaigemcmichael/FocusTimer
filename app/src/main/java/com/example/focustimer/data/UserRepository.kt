package com.example.focustimer.data

class UserRepository(private val userDao: UserDao) {
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
}
