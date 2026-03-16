package com.example.focustimer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.focustimer.data.viewmodel.UserViewModel
import com.example.focustimer.databinding.FragmentUpdateUserBinding
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

private const val TAG = "UpdateUserFragment"

class UpdateUserFragment : Fragment() {

    private var _binding: FragmentUpdateUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "UpdateUserFragment onCreateView() called")
        _binding = FragmentUpdateUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "UpdateUserFragment onViewCreated() called")

        // Pre-fill current user's name
        viewModel.userResult.value?.let { user ->
            binding.nameBox.setText(user.name)
        }

        binding.updateButton.setOnClickListener {
            val newName = binding.nameBox.text.toString()
            val newPassword = binding.passwordBox.text.toString()
            val currentUser = viewModel.userResult.value

            if (newName.isBlank() || newPassword.isBlank()) {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (currentUser != null) {
                try {
                    val digest = MessageDigest.getInstance("SHA-256")
                    val sha256HashBytes = digest.digest(newPassword.toByteArray(StandardCharsets.UTF_8))

                    // Standard Kotlin way to convert bytes to Hex string
                    val sha256HashStr = sha256HashBytes.joinToString("") { "%02x".format(it) }

                    val updatedUser = currentUser.copy(name = newName, password = sha256HashStr)
                    viewModel.updateUser(updatedUser)
                    //Debugging:
                    Toast.makeText(requireContext(), "User information updated.", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Log.e(TAG, "Error hashing password, loginFragment", e)
                    Toast.makeText(requireContext(), "Error hashing password, UpdateUserFragment", Toast.LENGTH_SHORT).show()

                }
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "UpdateUserFragment onDestroyView() called")
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "UpdateUserFragment onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "UpdateUserFragment onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "UpdateUserFragment onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "UpdateUserFragment onStop() called")
    }

    override fun onDestroy() {
        Log.d(TAG, "UpdateUserFragment onDestroy() called")
        super.onDestroy()
    }
}
