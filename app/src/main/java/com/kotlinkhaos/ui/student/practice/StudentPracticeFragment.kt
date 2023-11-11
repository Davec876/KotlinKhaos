package com.kotlinkhaos.ui.student.practice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kotlinkhaos.databinding.FragmentStudentPracticeBinding

class StudentPracticeFragment : Fragment() {
    private var _binding: FragmentStudentPracticeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentPracticeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPractice
        textView.text = "This is student practice Fragment"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}