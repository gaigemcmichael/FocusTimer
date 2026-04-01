package com.example.focustimer.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "focus_sessions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["userName"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FocusMethod::class,
            parentColumns = ["focusMethodId"],
            childColumns = ["focusMethodId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("userName"),
        Index("focusMethodId")
    ]
)
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val focusSessionId: Int = 0,
    val startTime: Date,
    val endTime: Date,
    val numPickups: Int,
    val numPauses: Int,
    val focusScore: Int,
    val numRounds: Int,
    val userName: String,
    val focusMethodId: String
)
