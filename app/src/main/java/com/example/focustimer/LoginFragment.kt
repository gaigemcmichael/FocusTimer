package com.example.focustimer

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.focustimer.databinding.FragmentLoginBinding
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpNavButton.paintFlags =
            binding.signUpNavButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.loginButton.setOnClickListener {
            val username = binding.usernameBox.text.toString()
            val password = binding.passwordBox.text.toString()

            findNavController().navigate(
                R.id.action_loginFragment_to_homeFragment
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}