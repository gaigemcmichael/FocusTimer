package com.example.focustimer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.focustimer.databinding.FragmentHomeBinding

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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

        viewModel.userResult.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // Extract only the first name (word) from the user's name
                val firstName = user.name.split(" ").firstOrNull() ?: ""
                binding.welcomeText.text = getString(R.string.home_page_focus_message, firstName)
            } else {
                Log.d(TAG, "User is null in HomeFragment")
            }
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
