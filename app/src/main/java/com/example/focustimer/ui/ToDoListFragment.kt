package com.example.focustimer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.focustimer.R
import com.example.focustimer.data.model.Task
import com.example.focustimer.data.viewmodel.TaskViewModel
import com.example.focustimer.databinding.FragmentToDoListBinding
import java.text.SimpleDateFormat
import java.util.Locale

private const val TAG = "ToDoListFragment"

class ToDoListFragment : Fragment() {

    private var _binding: FragmentToDoListBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by viewModels()

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

        taskViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            val adapter = TaskAdapter(tasks)
            binding.taskRecyclerView.swapAdapter(adapter, true)
        }
    }

    private class TaskHolder(inflater: LayoutInflater, parent: ViewGroup?) :
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
            statusTextView.text = task.status.uppercase()
        }
    }

    private inner class TaskAdapter(private val mTaskList: List<Task>) :
        RecyclerView.Adapter<TaskHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
            val inflater = LayoutInflater.from(requireContext())
            return TaskHolder(inflater, parent)
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
