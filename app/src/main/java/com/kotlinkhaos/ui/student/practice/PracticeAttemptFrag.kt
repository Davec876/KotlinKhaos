package com.kotlinkhaos.ui.student.practice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.classes.errors.InstructorQuizError
import com.kotlinkhaos.classes.errors.PracticeQuizError
import com.kotlinkhaos.classes.errors.StudentQuizError
import com.kotlinkhaos.classes.practiceQuiz.PracticeQuiz
import com.kotlinkhaos.databinding.FragmentPracticeAttemptBinding
import com.kotlinkhaos.ui.instructor.home.QuizsForCourseListAdapter
import kotlinx.coroutines.launch

class PracticeAttemptFrag : Fragment() {
    private var _binding: FragmentPracticeAttemptBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var practiceQuiz: PracticeQuiz

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
            val userAnswer = binding.practiceQuizAnswer.text.toString().trim()
            lifecycleScope.launch {
                try {
                    practiceQuiz.sendAnswer(userAnswer)
                    val feedback = practiceQuiz.getFeedback()
                    binding.practiceQuizQuestionNumber.text = "Feedback for Question ${practiceQuiz.getCurrentQuestionNumber()}"
                    binding.practiceQuizQuestion.text = feedback

                    binding.buttonNextQuestion.visibility = View.VISIBLE
                } catch (err: Exception) {
                    if (err is FirebaseAuthError || err is PracticeQuizError) {
//                        binding.errorMessage.text = err.message
                        return@launch
                    }
                    throw err
                }
            }
        }
        binding.buttonNextQuestion.setOnClickListener {
            loadNextQuestion()
        }

        return root
    }

    private fun loadNextQuestion() {
        lifecycleScope.launch {
            try {
                val nextQuestion = practiceQuiz.continuePracticeQuiz()
                if (nextQuestion) {
                    binding.practiceQuizQuestion.text = practiceQuiz.getQuestion()
                    binding.practiceQuizQuestionNumber.text = "Question ${practiceQuiz.getCurrentQuestionNumber()}"
                    binding.practiceQuizAnswer.text.clear()
                    // Hide the Next button until next feedback
                    binding.buttonNextQuestion.visibility = View.GONE
                }
                else {
                    // Handle the end of quiz scenario
                    binding.practiceQuizQuestion.text = "${practiceQuiz.getFinalScore()}/10"
                    binding.practiceQuizQuestionNumber.text = "Final Score"
                    binding.practiceQuizAnswer.text.clear()
                    binding.buttonNextQuestion.visibility = View.GONE

                }
            } catch (err: Exception) {
                if (err is FirebaseAuthError || err is PracticeQuizError) {
//                        binding.errorMessage.text = err.message
                    return@launch
                }
                throw err
            }
        }
    }

    private fun startPracticeQuiz() {
        var string = arguments?.getString("prompt")
        val prompt = if (string != null) string else ""
        lifecycleScope.launch {
            try {
                practiceQuiz = PracticeQuiz.start(prompt)
//                println(quiz.getQuestion())
                binding.practiceQuizQuestion.text = practiceQuiz.getQuestion()
            } catch (err: Exception) {
                if (err is FirebaseAuthError || err is PracticeQuizError) {
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