package com.example.focustimer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_methods")
data class FocusMethod(
    @PrimaryKey val focusMethodId: String, // e.g., "POMODORO", "CLASSIC", "FLOWMODORO"
    val description: String,
    val focusDuration: Int, // in minutes
    val breakDuration: Int // in minutes
)
