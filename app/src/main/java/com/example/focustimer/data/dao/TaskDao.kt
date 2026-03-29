package com.example.focustimer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.focustimer.data.model.Task
import com.example.focustimer.data.model.TaskStatus

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId AND status != :status ORDER BY dueDate ASC")
    fun getIncompleteTasksLiveData(userId: String, status: TaskStatus = TaskStatus.COMPLETED): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND status != :status ORDER BY dueDate ASC")
    suspend fun getIncompleteTasks(userId: String, status: TaskStatus = TaskStatus.COMPLETED): List<Task>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND status = :status ORDER BY dueDate DESC")
    fun getCompletedTasksLiveData(userId: String, status: TaskStatus = TaskStatus.COMPLETED): LiveData<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    @Query("DELETE FROM tasks WHERE userId = :userId")
    suspend fun deleteTasksByUserId(userId: String)
}
