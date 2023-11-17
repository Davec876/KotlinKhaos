package com.kotlinkhaos.ui.student.practice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kotlinkhaos.databinding.FragmentPracticeAttemptBinding
import com.kotlinkhaos.databinding.FragmentStudentPracticeBinding

class PracticeAttemptFrag : Fragment() {
    private var _binding: FragmentPracticeAttemptBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPracticeAttemptBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPractice
        textView.text = "This is student practice Fragment"
        return root
    }

    //life cycle coroutine
    //call the methods relating to api
    //

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}