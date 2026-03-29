package com.example.focustimer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.focustimer.R
import com.example.focustimer.data.model.Task
import com.example.focustimer.data.viewmodel.TaskViewModel
import com.example.focustimer.data.viewmodel.UserViewModel
import com.example.focustimer.databinding.FragmentToDoListBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.tasks.TasksScopes
import java.text.SimpleDateFormat
import java.util.Locale

private const val TAG = "ToDoListFragment"

class ToDoListFragment : Fragment() {

    private var _binding: FragmentToDoListBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "Google Sign-In result code: ${result.resultCode}")
        
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val email = account?.email
            Log.d(TAG, "Google Sign-In success. Email: $email")
            
            if (email != null) {
                userViewModel.linkGoogleAccount(email)
                Toast.makeText(requireContext(), "Tasks Linked: $email", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Google Sign-In failed. Status Code: ${e.statusCode}", e)
            val message = when (e.statusCode) {
                10 -> "Developer Error: Check SHA-1 and Client ID in Google Console"
                7 -> "Network Error"
                12500 -> "Sign-In Failed: Ensure Google Play Services are up to date"
                else -> "Error: ${e.statusCode}"
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "ToDoListFragment onCreateView() called")
        _binding = FragmentToDoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "ToDoListFragment onViewCreated() called")

        binding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        taskViewModel.incompleteTasks.observe(viewLifecycleOwner) { tasks ->
            binding.noTasksText.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
            val adapter = TaskAdapter(tasks)
            binding.taskRecyclerView.swapAdapter(adapter, true)
        }

        binding.addTaskButton.setOnClickListener {
            findNavController().navigate(R.id.action_toDoListFragment_to_createTaskFragment)
        }

        binding.completedTasksButton.setOnClickListener {
            findNavController().navigate(R.id.action_toDoListFragment_to_completedTasksFragment)
        }

        binding.linkCalendarButton.setOnClickListener {
            startGoogleSignIn()
        }
        
        // Auto-sync if account is already linked
        userViewModel.userResult.observe(viewLifecycleOwner) { user ->
            user?.googleAccountName?.let { email ->
                Log.d(TAG, "Account linked: $email. Triggering sync.")
                taskViewModel.syncWithGoogleTasks(email)
            }
        }
    }

    private fun startGoogleSignIn() {
        Log.d(TAG, "Starting Google Sign-In...")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(TasksScopes.TASKS))
            .requestIdToken(getString(R.string.google_client_id))
            .build()

        val client = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInLauncher.launch(client.signInIntent)
    }

    private class TaskHolder(inflater: LayoutInflater, parent: ViewGroup?, private val onClick: (Task) -> Unit) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_task, parent, false)) {
        
        private val titleTextView: TextView = itemView.findViewById(R.id.task_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.task_description)
        private val dueDateTextView: TextView = itemView.findViewById(R.id.task_due_date)
        private val statusTextView: TextView = itemView.findViewById(R.id.task_status)
        
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(task: Task) {
            titleTextView.text = task.title
            descriptionTextView.text = task.description
            dueDateTextView.text = itemView.context.getString(
                R.string.due_date, dateFormat.format(task.dueDate)
            )
            statusTextView.text = task.status.description
            
            itemView.setOnClickListener { onClick(task) }
        }
    }

    private inner class TaskAdapter(private val mTaskList: List<Task>) :
        RecyclerView.Adapter<TaskHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
            val inflater = LayoutInflater.from(requireContext())
            return TaskHolder(inflater, parent) { task ->
                val action = ToDoListFragmentDirections.actionToDoListFragmentToUpdateTaskFragment(task.id)
                findNavController().navigate(action)
            }
        }

        override fun onBindViewHolder(holder: TaskHolder, position: Int) {
            val task = mTaskList[position]
            holder.bind(task)
        }

        override fun getItemCount(): Int = mTaskList.size
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "ToDoListFragment onDestroyView() called")
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "ToDoListFragment onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "ToDoListFragment onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "ToDoListFragment onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "ToDoListFragment onStop() called")
    }

    override fun onDestroy() {
        Log.d(TAG, "ToDoListFragment onDestroy() called")
        super.onDestroy()
    }
}
