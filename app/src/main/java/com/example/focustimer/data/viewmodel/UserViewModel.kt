package com.example.focustimer.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.focustimer.data.FocusTimerApplication
import com.example.focustimer.data.model.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as FocusTimerApplication).repository

    val userResult = MutableLiveData<User?>()
    val errorMessage = MutableLiveData<String?>()
    val deleteSuccess = MutableLiveData<Boolean>()

    fun signUp(username: String, name: String, password: String) {
        viewModelScope.launch {
            val existingUser = repository.getUserByUsername(username)
            if (existingUser != null) {
                errorMessage.value = "Username already exists"
            } else {
                val newUser = User(username, name, password)
                repository.insertUser(newUser)
                userResult.value = newUser
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username)
            if (user != null && user.password == password) {
                userResult.value = user
            } else {
                errorMessage.value = "Invalid username or password"
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
            userResult.value = user
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user)
            userResult.value = null
            deleteSuccess.value = true
        }
    }
}