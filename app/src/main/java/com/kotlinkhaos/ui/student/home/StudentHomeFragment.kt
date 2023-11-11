package com.kotlinkhaos.ui.student.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kotlinkhaos.classes.User
import com.kotlinkhaos.databinding.FragmentStudentHomeBinding
import com.kotlinkhaos.ui.auth.AuthActivity

class StudentHomeFragment : Fragment() {
    private var _binding: FragmentStudentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.logoutButton.setOnClickListener {
            handleLogout()
        }

        val textView: TextView = binding.textHome
        textView.text = "This is student home Fragment"
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
}