package com.kotlinkhaos.ui.student.practice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.classes.errors.InstructorQuizError
import com.kotlinkhaos.classes.practiceQuiz.PracticeQuiz
import com.kotlinkhaos.databinding.FragmentPracticeAttemptBinding
import kotlinx.coroutines.launch

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

        startPracticeQuiz()

        //converting the edit text to a string
        binding.buttonGetFeedback.setOnClickListener {
            val userAnswer = binding.practiceQuizAnswer.text.toString()
        }

        return root
    }

    private fun startPracticeQuiz() {
        var string = arguments?.getString("inputText")
        val inputText = if (string != null) string else ""
        lifecycleScope.launch {
            try {
                val quiz = PracticeQuiz.start(inputText)
//                println(quiz.getQuestion())
                binding.practiceQuizQuestion.text = quiz.getQuestion()
                binding.practiceQuizAnswer.text = quiz.getFeedback()
            } catch (err: Exception) {
                if (err is FirebaseAuthError || err is InstructorQuizError) {
//                    binding.errorMessage.text = err.message
                    return@launch
                }
                throw err
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}