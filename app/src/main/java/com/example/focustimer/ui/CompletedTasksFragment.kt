package com.example.focustimer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.focustimer.R
import com.example.focustimer.data.model.Task
import com.example.focustimer.data.viewmodel.TaskViewModel
import com.example.focustimer.databinding.FragmentCompletedTasksBinding
import java.text.SimpleDateFormat
import java.util.Locale

private const val TAG = "CompletedTasksFragment"

class CompletedTasksFragment : Fragment() {

    private var _binding: FragmentCompletedTasksBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "CompletedTasksFragment onCreateView() called")
        _binding = FragmentCompletedTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "CompletedTasksFragment onViewCreated() called")

        binding.completedTaskRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        taskViewModel.completedTasks.observe(viewLifecycleOwner) { tasks ->
            val adapter = CompletedTaskAdapter(tasks)
            binding.completedTaskRecyclerView.swapAdapter(adapter, true)
        }
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
            // Display the enum description
            statusTextView.text = task.status.description
            
            itemView.setOnClickListener { onClick(task) }
        }
    }

    private inner class CompletedTaskAdapter(private val mTaskList: List<Task>) :
        RecyclerView.Adapter<TaskHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
            val inflater = LayoutInflater.from(requireContext())
            return TaskHolder(inflater, parent) { task ->
                val action = CompletedTasksFragmentDirections.actionCompletedTasksFragmentToUpdateTaskFragment(task.id)
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
        Log.d(TAG, "CompletedTasksFragment onDestroyView() called")
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "CompletedTasksFragment onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "CompletedTasksFragment onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "CompletedTasksFragment onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "CompletedTasksFragment onStop() called")
    }

    override fun onDestroy() {
        Log.d(TAG, "CompletedTasksFragment onDestroy() called")
        super.onDestroy()
    }
}
