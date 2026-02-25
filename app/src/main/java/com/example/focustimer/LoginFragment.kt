package com.example.focustimer

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.focustimer.databinding.FragmentLoginBinding
import androidx.navigation.fragment.findNavController

private const val TAG = "LoginFragment"
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


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

            // handle username/password validation against saved user data

            findNavController().navigate(
                R.id.action_loginFragment_to_homeFragment
            )
        }

        binding.signUpNavButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_signUpFragment
            )
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
        super.onDestroy()
        Log.d(TAG, "LoginFragment onDestroy() called")
    }
}