package com.example.focustimer.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "tasks",
    indices = [Index(value = ["googleEventId"], unique = true)]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val title: String,
    val description: String,
    val createDate: Date,
    val dueDate: Date,
    val status: TaskStatus,
    val googleEventId: String? = null
)
