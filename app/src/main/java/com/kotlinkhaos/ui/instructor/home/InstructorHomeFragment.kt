package com.kotlinkhaos.ui.instructor.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kotlinkhaos.classes.User
import com.kotlinkhaos.databinding.FragmentInstructorHomeBinding
import com.kotlinkhaos.ui.auth.AuthActivity

class InstructorHomeFragment : Fragment() {
    private var _binding: FragmentInstructorHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstructorHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.logoutButton.setOnClickListener {
            handleLogout()
        }
        binding.addQuizFAB.setOnClickListener {
            handleAddQuiz()
        }

        val textView: TextView = binding.textHome
        textView.text = "This is instructor home Fragment"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleLogout() {
        User.logout()
        val intent = Intent(requireActivity(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun handleAddQuiz() {
        val action = InstructorHomeFragmentDirections.startNavigationCreateQuiz()
        findNavController().navigate(action)
    }
}