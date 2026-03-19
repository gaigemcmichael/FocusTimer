package com.example.focustimer.data.model

import java.util.Date

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val createDate: Date,
    val dueDate: Date,
    val status: String //could make an enum in the future
)
