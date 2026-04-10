package com.example.focustimer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.focustimer.R
import com.example.focustimer.data.FocusTimerApplication
import com.example.focustimer.data.model.FocusSession
import com.example.focustimer.data.viewmodel.UserViewModel
import com.example.focustimer.databinding.FragmentSessionHistoryBinding
import com.example.focustimer.databinding.ItemSessionBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class SessionHistoryFragment : Fragment() {

    private var _binding: FragmentSessionHistoryBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var adapter: SessionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SessionAdapter()
        binding.sessionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.sessionRecyclerView.adapter = adapter

        val username = userViewModel.userResult.value?.username
        if (username != null) {
            loadSessions(username)
        }
    }

    private fun loadSessions(username: String) {
        val repository = (requireActivity().application as FocusTimerApplication).timerRepository
        lifecycleScope.launch {
            val sessions = repository.getSessionsByUsername(username).sortedByDescending { it.startTime }
            adapter.submitList(sessions)
            binding.noSessionsText.isVisible = sessions.isEmpty()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class SessionAdapter : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {
        private var sessions = listOf<FocusSession>()

        fun submitList(newSessions: List<FocusSession>) {
            sessions = newSessions
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
            val binding = ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SessionViewHolder(binding)
        }

        override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
            holder.bind(sessions[position])
        }

        override fun getItemCount() = sessions.size

        inner class SessionViewHolder(private val itemBinding: ItemSessionBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

            fun bind(session: FocusSession) {
                itemBinding.sessionMethod.text = when(session.focusMethodId.uppercase()) {
                    "CLASSIC" -> getString(R.string.classic_method)
                    "POMODORO" -> getString(R.string.pomodoro_method)
                    "FLOWMODORO" -> getString(R.string.flowmodoro_method)
                    else -> session.focusMethodId
                }

                itemBinding.sessionScore.text = getString(R.string.session_score_label, session.focusScore)
                itemBinding.sessionDate.text = dateFormat.format(session.startTime)
                
                val durationMillis = session.endTime.time - session.startTime.time
                val minutes = (durationMillis / 1000) / 60
                val seconds = (durationMillis / 1000) % 60
                itemBinding.sessionDuration.text = getString(R.string.session_duration_label, minutes, seconds)
                
                itemBinding.sessionDetails.text = getString(R.string.session_details_label, session.numRounds, session.numPickups)
            }
        }
    }
}
