package com.example.focustimer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.focustimer.databinding.FragmentFocusSelectionBinding

private const val TAG = "FocusSelectionFragment"

class FocusSelectionFragment : Fragment() {

    private var _binding: FragmentFocusSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "FocusSelectionFragment onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "FocusSelectionFragment onCreateView() called")
        _binding = FragmentFocusSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "FocusSelectionFragment onViewCreated() called")

        binding.classicButton.setOnClickListener {
            // Handle Classic selection
        }

        binding.pomodoroButton.setOnClickListener {
            // Handle Pomodoro selection
        }

        binding.flowmodoroButton.setOnClickListener {
            // Handle Flowmodoro selection
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "FocusSelectionFragment onDestroyView() called")
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "FocusSelectionFragment onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "FocusSelectionFragment onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "FocusSelectionFragment onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "FocusSelectionFragment onStop() called")
    }

    override fun onDestroy() {
        Log.d(TAG, "FocusSelectionFragment onDestroy() called")
        super.onDestroy()
    }
}
