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
import java.util.Date

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

    suspend fun fetchGoogleTasks(accountName: String, userId: String): List<Task> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching tasks for: $accountName")
            val service = getTasksService(accountName)
            
            val tasks = service.tasks().list("@default").execute()

            Log.d(TAG, "Fetched ${tasks.items?.size ?: 0} tasks")
            tasks.items?.map { googleTask ->
                Task(
                    userId = userId,
                    title = googleTask.title ?: "No Title",
                    description = googleTask.notes ?: "",
                    createDate = Date(),
                    // Google Tasks 'due' is an RFC 3339 timestamp string
                    dueDate = googleTask.due?.let { Date(DateTime.parseRfc3339(it).value) } ?: Date(),
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
                // Ensure date is in RFC 3339 format string
                due = DateTime(task.dueDate).toStringRfc3339()
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

    suspend fun updateGoogleTask(accountName: String, task: Task) = withContext(Dispatchers.IO) {
        val googleEventId = task.googleEventId ?: return@withContext
        try {
            Log.d(TAG, "Updating task in Google Tasks: ${task.title}")
            val service = getTasksService(accountName)
            
            val googleTask = service.tasks().get("@default", googleEventId).execute().apply {
                title = task.title
                notes = task.description
                due = DateTime(task.dueDate).toStringRfc3339()
                status = if (task.status == TaskStatus.COMPLETED) "completed" else "needsAction"
                if (task.status == TaskStatus.COMPLETED) {
                    completed = DateTime(System.currentTimeMillis()).toStringRfc3339()
                }
            }

            service.tasks().update("@default", googleEventId, googleTask).execute()
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
