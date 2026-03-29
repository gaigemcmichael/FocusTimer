package com.example.focustimer.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.focustimer.data.model.Task
import com.example.focustimer.data.model.TaskStatus
import com.example.focustimer.data.viewmodel.TaskViewModel
import com.example.focustimer.data.viewmodel.UserViewModel
import com.example.focustimer.databinding.FragmentUpdateTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val TAG = "UpdateTaskFragment"

class UpdateTaskFragment : Fragment() {

    private var _binding: FragmentUpdateTaskBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val args: UpdateTaskFragmentArgs by navArgs()
    
    private var selectedDueDate: Date? = null
    private val calendar = Calendar.getInstance()
    private var currentTask: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView() called")
        _binding = FragmentUpdateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        setupStatusSpinner()

        taskViewModel.loadTaskById(args.taskId)
        taskViewModel.taskToUpdate.observe(viewLifecycleOwner) { task ->
            if (task != null) {
                currentTask = task
                populateFields(task)
            }
        }

        binding.datePickerButton.setOnClickListener {
            showDatePicker()
        }

        binding.updateTaskButton.setOnClickListener {
            updateTask()
        }

        binding.deleteTaskButton.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun setupStatusSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            TaskStatus.entries.map { it.description }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.statusSpinner.adapter = adapter
    }

    private fun populateFields(task: Task) {
        binding.titleEditText.setText(task.title)
        binding.descriptionEditText.setText(task.description)
        
        selectedDueDate = task.dueDate
        calendar.time = task.dueDate
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        binding.selectedDateText.text = dateFormat.format(task.dueDate)

        val statusEntries = TaskStatus.entries
        val position = statusEntries.indexOfFirst { it == task.status }
        if (position >= 0) {
            binding.statusSpinner.setSelection(position)
        }

        // Hide loading text and show spinner once data is loaded
        binding.statusLoadingText.visibility = View.GONE
        binding.statusSpinner.visibility = View.VISIBLE
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                selectedDueDate = calendar.time
                
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                binding.selectedDateText.text = dateFormat.format(selectedDueDate!!)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateTask() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        
        val selectedDescription = binding.statusSpinner.selectedItem.toString()
        val status = TaskStatus.entries.first { it.description == selectedDescription }

        if (title.isBlank()) {
            Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
            return
        }

        currentTask?.let { task ->
            val updatedTask = task.copy(
                title = title,
                description = description,
                dueDate = selectedDueDate ?: task.dueDate,
                status = status
            )
            
            val googleAccount = userViewModel.userResult.value?.googleAccountName
            taskViewModel.updateTask(updatedTask, googleAccount)
            
            Toast.makeText(requireContext(), "Task Updated", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Delete") { _, _ ->
                currentTask?.let {
                    // get account name (email) and pass to delete to delete from correct google account
                    val googleAccount = userViewModel.userResult.value?.googleAccountName
                    taskViewModel.deleteTask(it, googleAccount)
                    Toast.makeText(requireContext(), "Task Deleted", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy() called")
        super.onDestroy()
    }
}
