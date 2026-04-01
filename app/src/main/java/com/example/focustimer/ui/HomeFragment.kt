package com.example.focustimer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.focustimer.R
import com.example.focustimer.data.viewmodel.TaskViewModel
import com.example.focustimer.data.viewmodel.UserViewModel
import com.example.focustimer.databinding.FragmentHomeBinding

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private val taskViewModel: TaskViewModel by activityViewModels()

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

        userViewModel.userResult.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // Extract only the first name (word) from the user's name
                val firstName = user.name.split(" ").firstOrNull() ?: ""
                binding.welcomeText.text = getString(R.string.home_page_focus_message, firstName)
            } else {
                Log.d(TAG, "User is null in HomeFragment")
            }
        }

        taskViewModel.tasksDueNextWeek.observe(viewLifecycleOwner) { count ->
            binding.homePageTasksMessage.text = getString(R.string.home_page_tasks_message, count)
        }

        binding.startAFocusSessionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_focusSelectionFragment)
        }

        binding.toDoListButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_toDoListFragment)
        }

        binding.updateAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_updateUserFragment)
        }

        binding.logOutButton.setOnClickListener {
            logOut()
        }

        binding.deleteAccountButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    userViewModel.userResult.value?.let { user ->
                        taskViewModel.deleteUserTasks(user.username)
                        userViewModel.deleteUser(user)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        userViewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                userViewModel.deleteSuccess.value = false
                Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }
    }

    private fun logOut() {
        userViewModel.userResult.value = null
        taskViewModel.setCurrentUser(null)
        
        Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
        
        // Navigate back to login and clear home from backstack
        findNavController().navigate(
            R.id.action_homeFragment_to_loginFragment,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, true)
                .build()
        )
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
