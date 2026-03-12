package com.example.focustimer

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.focustimer.databinding.FragmentLoginBinding

private const val TAG = "LoginFragment"

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Use activityViewModels to share state with HomeFragment
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "LoginFragment onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "LoginFragment OnCreateView() called")
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "LoginFragment OnViewCreated() called")
        binding.signUpNavButton.paintFlags =
            binding.signUpNavButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.loginButton.setOnClickListener {
            val username = binding.usernameBox.text.toString()
            val password = binding.passwordBox.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.login(username, password)
            }
        }

        binding.signUpNavButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_signUpFragment
            )
        }

        viewModel.userResult.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                findNavController().navigate(
                    R.id.action_loginFragment_to_homeFragment,
                    null,
                    androidx.navigation.NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true)
                        .build()
                )
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "LoginFragment onDestroyView() called")
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "LoginFragment onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "LoginFragment onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "LoginFragment onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "LoginFragment onStop() called")
    }

    override fun onDestroy() {
        Log.d(TAG, "LoginFragment onDestroy() called")
        super.onDestroy()
    }
}
