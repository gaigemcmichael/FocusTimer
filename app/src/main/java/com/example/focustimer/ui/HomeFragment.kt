package com.example.focustimer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.focustimer.R
import com.example.focustimer.data.FocusTimerApplication
import com.example.focustimer.data.viewmodel.UserViewModel
import com.example.focustimer.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Use activityViewModels to share the ViewModel with Login/SignUp
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "HomeFragment onCreateView() called")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "HomeFragment onViewCreated() called")

        // Observe the userResult to update UI
        viewModel.userResult.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.welcomeText.text = getString(R.string.home_page_focus_message, user.name)
                loadDashboardStats(user.username)
            } else {
                // If user is null, navigate back to login (security check)
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }

        binding.startAFocusSessionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_focusSelectionFragment)
        }

        binding.toDoListButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_toDoListFragment)
        }

        binding.sessionHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_sessionHistoryFragment)
        }

        binding.updateAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_updateUserFragment)
        }

        binding.deleteAccountButton.setOnClickListener {
            val user = viewModel.userResult.value
            if (user != null) {
                viewModel.deleteUser(user)
            }
        }

        binding.logOutButton.setOnClickListener {
            viewModel.userResult.value = null
            // Navigation handled by the observer
        }

        viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }
    }

    private fun loadDashboardStats(username: String) {
        val app = requireActivity().application as FocusTimerApplication
        val timerRepo = app.timerRepository
        val taskRepo = app.taskRepository

        lifecycleScope.launch {
            // 1. Calculate Average Focus Score
            val sessions = timerRepo.getSessionsByUsername(username)
            val averageScore = if (sessions.isNotEmpty()) {
                sessions.map { it.focusScore }.average().toInt()
            } else {
                0
            }
            binding.homePageFocusScoreText.text = getString(R.string.home_page_focus_score_message, averageScore)

            // 2. Calculate Tasks Due in Next Week
            val tasks = taskRepo.getIncompleteTasks(username)
            
            // Set 'now' to the beginning of today (00:00:00) to include tasks due today
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfToday = calendar.time

            // Set the end of the range to 7 days from now (inclusive of that 7th day)
            calendar.add(Calendar.DAY_OF_YEAR, 8) 
            val endOfNextWeek = calendar.time

            val tasksDueNextWeek = tasks.count { 
                val dueDate = it.dueDate
                !dueDate.before(startOfToday) && dueDate.before(endOfNextWeek)
            }
            binding.homePageTasksMessage.text = getString(R.string.home_page_tasks_message, tasksDueNextWeek)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "HomeFragment onDestroyView() called")
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "HomeFragment onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "HomeFragment onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "HomeFragment onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "HomeFragment onStop() called")
    }

    override fun onDestroy() {
        Log.d(TAG, "HomeFragment onDestroy() called")
        super.onDestroy()
    }
}
