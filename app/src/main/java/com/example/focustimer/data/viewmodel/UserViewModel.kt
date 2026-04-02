package com.example.focustimer.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.focustimer.data.FocusTimerApplication
import com.example.focustimer.data.model.User
import kotlinx.coroutines.launch

private const val TAG = "UserViewModel"

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as FocusTimerApplication).repository
    private val taskRepository = (application as FocusTimerApplication).taskRepository

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
            // Delete user's tasks first
            taskRepository.deleteTasksByUserId(user.username)
            // Then delete the user
            repository.deleteUser(user)
            userResult.value = null
            deleteSuccess.value = true
        }
    }

    fun linkGoogleAccount(googleAccountName: String) {
        Log.d(TAG, "linkGoogleAccount called with: $googleAccountName")
        viewModelScope.launch {
            val currentUser = userResult.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(googleAccountName = googleAccountName)
                repository.updateUser(updatedUser)
                userResult.value = updatedUser
                Log.d(TAG, "Successfully linked account: $googleAccountName to user: ${currentUser.username}")
            } else {
                Log.e(TAG, "Cannot link account: userResult.value is NULL")
            }
        }
    }
}
