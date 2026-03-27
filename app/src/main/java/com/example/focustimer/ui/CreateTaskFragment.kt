package com.example.focustimer.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.focustimer.data.viewmodel.TaskViewModel
import com.example.focustimer.databinding.FragmentCreateTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val TAG = "CreateTaskFragment"

class CreateTaskFragment : Fragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by viewModels()
    private var selectedDueDate: Date? = null
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "CreateTaskFragment onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "CreateTaskFragment onCreateView() called")
        _binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "CreateTaskFragment onViewCreated() called")

        binding.datePickerButton.setOnClickListener {
            showDatePicker()
        }

        binding.saveTaskButton.setOnClickListener {
            saveTask()
        }
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

    private fun saveTask() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()

        if (title.isBlank()) {
            Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDueDate == null) {
            Toast.makeText(requireContext(), "Please select a due date", Toast.LENGTH_SHORT).show()
            return
        }

        taskViewModel.addTask(title, description, selectedDueDate!!)
        Toast.makeText(requireContext(), "Task Saved", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "CreateTaskFragment onDestroyView() called")
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "CreateTaskFragment onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "CreateTaskFragment onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "CreateTaskFragment onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "CreateTaskFragment onStop() called")
    }

    override fun onDestroy() {
        Log.d(TAG, "CreateTaskFragment onDestroy() called")
        super.onDestroy()
    }
}
