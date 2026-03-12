package com.example.focustimer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.focustimer.data.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as FocusTimerApplication).repository

    val userResult = MutableLiveData<User?>()
    val errorMessage = MutableLiveData<String?>()

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
            // rework authentication to actually check hashed passwords match or another auth method
            if (user != null && user.password == password) {
                userResult.value = user
            } else {
                errorMessage.value = "Invalid username or password"
            }
        }
    }
}
