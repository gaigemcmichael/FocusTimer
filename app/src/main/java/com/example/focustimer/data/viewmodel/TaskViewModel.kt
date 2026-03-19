package com.example.focustimer.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.focustimer.data.model.Task
import java.util.Calendar

class TaskViewModel : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    init {
        loadFakeData()
    }

    private fun loadFakeData() {
        val calendar = Calendar.getInstance()
        val today = calendar.time
        
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = calendar.time

        val fakeTasks = listOf(
            Task(1, "Complete CSE5236 Project", "Finish the Focus Timer app implementation", today, tomorrow, "Pending"),
            Task(2, "Buy Groceries", "Get milk, eggs, and bread", today, today, "Pending"),
            Task(3, "Gym Session", "Leg day workout", today, tomorrow, "Completed"),
            Task(4, "Read Book", "Read 20 pages of 'Atomic Habits'", today, tomorrow, "Pending")
        )
        _tasks.value = fakeTasks
    }
}
