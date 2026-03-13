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
import com.example.focustimer.databinding.FragmentSignUpBinding
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

private const val TAG = "SignUpFragment"

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "SignUpFragment onCreate() called")
    }

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
        binding.loginNavButton.paintFlags =
            binding.loginNavButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.signUpButton.setOnClickListener {
            val username = binding.usernameBox.text.toString()
            val name = binding.nameBox.text.toString()
            val password = binding.passwordBox.text.toString()

            if (username.isBlank() || name.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                try {
                    val digest = MessageDigest.getInstance("SHA-256")
                    val sha256HashBytes = digest.digest(password.toByteArray(StandardCharsets.UTF_8))

                    // Standard Kotlin way to convert bytes to Hex string
                    val sha256HashStr = sha256HashBytes.joinToString("") { "%02x".format(it) }

                    viewModel.signUp(username, name, sha256HashStr)
                    //Debugging:
                    // Toast.makeText(requireContext(), "New UserAccount $username added", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Log.e(TAG, "Error hashing password", e)
                    Toast.makeText(requireContext(), "Error hashing password", Toast.LENGTH_SHORT).show()

                }

            }
        }

        binding.loginNavButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_signUpFragment_to_loginFragment
            )
        }

        viewModel.userResult.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                findNavController().navigate(
                    R.id.action_signUpFragment_to_homeFragment,
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
        Log.d(TAG, "SignUpFragment onDestroy() called")
        super.onDestroy()
    }

}