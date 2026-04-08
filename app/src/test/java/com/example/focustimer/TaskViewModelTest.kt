package com.example.focustimer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.focustimer.data.FocusTimerApplication
import com.example.focustimer.data.GoogleTasksService
import com.example.focustimer.data.model.Task
import com.example.focustimer.data.model.TaskStatus
import com.example.focustimer.data.repository.TaskRepository
import com.example.focustimer.data.viewmodel.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockApplication: FocusTimerApplication
    @Mock
    private lateinit var mockRepository: TaskRepository
    @Mock
    private lateinit var mockTasksService: GoogleTasksService

    private lateinit var viewModel: TaskViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        whenever(mockApplication.taskRepository).thenReturn(mockRepository)
        whenever(mockApplication.googleTasksService).thenReturn(mockTasksService)
        
        viewModel = TaskViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    // test correct tasks are loaded into LiveData for each user
    fun testUserSessionIsolation() {
        val userA = "UserA"
        val userB = "UserB"
        
        val tasksA = listOf(Task(1, userA, "Task A", "", Date(), Date(), TaskStatus.NEW))
        val tasksB = listOf(Task(2, userB, "Task B", "", Date(), Date(), TaskStatus.NEW))

        val liveDataA = MutableLiveData<List<Task>>()
        liveDataA.value = tasksA
        
        val liveDataB = MutableLiveData<List<Task>>()
        liveDataB.value = tasksB

        whenever(mockRepository.getIncompleteTasksLiveData(userA)).thenReturn(liveDataA)
        whenever(mockRepository.getIncompleteTasksLiveData(userB)).thenReturn(liveDataB)

        viewModel.incompleteTasks.observeForever { }

        viewModel.setCurrentUser(userA)
        assertEquals("Task A", viewModel.incompleteTasks.value?.first()?.title)

        viewModel.setCurrentUser(userB)
        assertEquals("Task B", viewModel.incompleteTasks.value?.first()?.title)
    }

    @Test
    // test duplicate task prevention by tasks with the same title
    fun testDuplicatePrevention() = runTest {
        val userId = "testUser"
        val googleAccount = "test@gmail.com"
        viewModel.setCurrentUser(userId)

        val existingTaskOnGoogle = Task(1, userId, "Sync Test", "", Date(), Date(), TaskStatus.NEW, "google_id_123")
        val localTaskWithoutId = Task(2, userId, "Sync Test", "", Date(), Date(), TaskStatus.NEW, null)

        whenever(mockTasksService.fetchGoogleTasks(eq(googleAccount), eq(userId))).thenReturn(listOf(existingTaskOnGoogle))
        whenever(mockRepository.getIncompleteTasks(userId)).thenReturn(listOf(localTaskWithoutId))

        viewModel.syncWithGoogleTasks(googleAccount)
        advanceUntilIdle() 

        // Verify that pushTaskToGoogle was never called because the title matched
        verify(mockTasksService, never()).pushTaskToGoogle(any(), any())
    }

    @Test
    // test that adding a task when a google account is present results in local and remote insertion
    fun testAddTaskFlow() = runTest {
        val userId = "testUser"
        val googleAccount = "test@gmail.com"
        viewModel.setCurrentUser(userId)
        
        val title = "New Task"
        val dueDate = Date()
        
        whenever(mockRepository.insert(any())).thenReturn(1L)

        viewModel.addTask(title, "Desc", dueDate, googleAccount)
        advanceUntilIdle()

        // Verify it was inserted locally
        verify(mockRepository).insert(argThat { task -> task.title == title && task.userId == userId })
        
        // Verify it was pushed to Google
        verify(mockTasksService).pushTaskToGoogle(eq(googleAccount), any())
    }
}
