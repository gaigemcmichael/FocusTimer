package com.example.focustimer.data

import android.content.Context
import android.util.Log
import com.example.focustimer.data.model.Task
import com.example.focustimer.data.model.TaskStatus
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.TasksScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

private const val TAG = "GoogleTasksService"

class GoogleTasksService(private val context: Context) {

    private val transport = NetHttpTransport()
    private val jsonFactory = GsonFactory.getDefaultInstance()

    private fun getTasksService(accountName: String): Tasks {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(TasksScopes.TASKS)
        ).setSelectedAccountName(accountName)

        return Tasks.Builder(transport, jsonFactory, credential)
            .setApplicationName("Focus Timer")
            .build()
    }

    /**
     * Normalizes a date to midnight in the LOCAL timezone.
     * Google Tasks due dates are returned as UTC midnight (e.g., 2023-10-10T00:00:00Z).
     * In negative timezones, converting this directly to local time shifts it to the previous day
     * (e.g., Oct 9th 19:00 in UTC-5). We extract the year/month/day in UTC and apply it locally.
     */
    private fun normalizeToLocalMidnight(date: Date): Date {
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utcCalendar.time = date
        
        val localCalendar = Calendar.getInstance()
        localCalendar.set(
            utcCalendar.get(Calendar.YEAR),
            utcCalendar.get(Calendar.MONTH),
            utcCalendar.get(Calendar.DAY_OF_MONTH),
            0, 0, 0
        )
        localCalendar.set(Calendar.MILLISECOND, 0)
        return localCalendar.time
    }

    /**
     * Formats a local date to UTC midnight for Google Tasks API.
     * This ensures the "day" selected by the user is preserved regardless of timezone offsets.
     */
    private fun formatDueDate(date: Date): String {
        val localCalendar = Calendar.getInstance()
        localCalendar.time = date
        
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utcCalendar.set(
            localCalendar.get(Calendar.YEAR),
            localCalendar.get(Calendar.MONTH),
            localCalendar.get(Calendar.DAY_OF_MONTH),
            0, 0, 0
        )
        utcCalendar.set(Calendar.MILLISECOND, 0)
        return DateTime(utcCalendar.time, TimeZone.getTimeZone("UTC")).toStringRfc3339()
    }

    suspend fun fetchGoogleTasks(accountName: String, userId: String): List<Task> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching tasks for: $accountName")
            val service = getTasksService(accountName)
            
            val tasks = service.tasks().list("@default").execute()

            Log.d(TAG, "Fetched ${tasks.items?.size ?: 0} tasks")
            tasks.items?.map { googleTask ->
                val googleDate = googleTask.due?.let { Date(DateTime.parseRfc3339(it).value) } ?: Date()
                
                // Google returns UTC midnight, normalize it to local midnight to avoid shifting days
                val localDate = normalizeToLocalMidnight(googleDate)

                Task(
                    userId = userId,
                    title = googleTask.title ?: "No Title",
                    description = googleTask.notes ?: "",
                    createDate = Date(),
                    dueDate = localDate,
                    status = if (googleTask.status == "completed") TaskStatus.COMPLETED else TaskStatus.NEW,
                    googleEventId = googleTask.id
                )
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching tasks", e)
            emptyList()
        }
    }

    suspend fun pushTaskToGoogle(accountName: String, task: Task): String? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Pushing task to Google Tasks: ${task.title}")
            val service = getTasksService(accountName)
            
            val googleTask = com.google.api.services.tasks.model.Task().apply {
                title = task.title
                notes = task.description
                // Google Tasks API only stores the DATE portion.
                due = formatDueDate(task.dueDate)
                status = if (task.status == TaskStatus.COMPLETED) "completed" else "needsAction"
            }

            val createdTask = service.tasks().insert("@default", googleTask).execute()
            Log.d(TAG, "Successfully created task with ID: ${createdTask.id}")
            createdTask.id
        } catch (e: Exception) {
            Log.e(TAG, "Error pushing task", e)
            null
        }
    }

    suspend fun updateGoogleTask(accountName: String, task: Task): Unit = withContext(Dispatchers.IO) {
        val googleEventId = task.googleEventId ?: return@withContext
        try {
            Log.d(TAG, "Patching task in Google Tasks: ${task.title}")
            val service = getTasksService(accountName)
            
            val taskPatch = com.google.api.services.tasks.model.Task().apply {
                title = task.title
                notes = task.description
                due = formatDueDate(task.dueDate)
                status = if (task.status == TaskStatus.COMPLETED) "completed" else "needsAction"
                if (task.status == TaskStatus.COMPLETED) {
                    completed = DateTime(System.currentTimeMillis()).toStringRfc3339()
                }
            }

            service.tasks().patch("@default", googleEventId, taskPatch).execute()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating task", e)
        }
    }

    suspend fun deleteGoogleTask(accountName: String, googleEventId: String): Unit = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Deleting task from Google Tasks ID: $googleEventId")
            val service = getTasksService(accountName)
            service.tasks().delete("@default", googleEventId).execute()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task", e)
        }
    }
}
