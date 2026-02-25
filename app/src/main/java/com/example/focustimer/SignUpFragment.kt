package com.example.focustimer

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.focustimer.databinding.FragmentLoginBinding
import com.example.focustimer.databinding.FragmentSignUpBinding


private const val TAG = "SignUpFragment"
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "SignUpFragment OnCreateView() called")
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "SignUpFragment onViewCreated() called")
        binding.loginNavButton.paintFlags = binding.loginNavButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.signUpButton.setOnClickListener {
            val username= binding.usernameBox.text.toString()
            val name = binding.nameBox.text.toString()
            val password = binding.passwordBox.text.toString()

            // handle username/name/password sign-up

            findNavController().navigate(
                R.id.action_signUpFragment_to_homeFragment
            )
        }

        binding.loginNavButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_signUpFragment_to_loginFragment
            )
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "SignUpFragment onDestroyView() called")
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "SignUpFragment onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "SignUpFragment onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "SignUpFragment onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "SignUpFragment onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "SignUpFragment onDestroy() called")
    }

}