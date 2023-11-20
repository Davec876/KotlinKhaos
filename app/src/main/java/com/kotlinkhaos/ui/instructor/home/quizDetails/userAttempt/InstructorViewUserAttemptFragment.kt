package com.kotlinkhaos.ui.instructor.home.quizDetails.userAttempt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.classes.errors.InstructorQuizDetailsError
import com.kotlinkhaos.classes.errors.InstructorQuizError
import com.kotlinkhaos.databinding.FragmentInstructorViewUserAttemptBinding
import com.kotlinkhaos.ui.instructor.home.QuizsForCourseViewModel

class InstructorViewUserAttemptFragment : Fragment() {
    private var _binding: FragmentInstructorViewUserAttemptBinding? = null
    private val quizsForCourseViewModel: QuizsForCourseViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentInstructorViewUserAttemptBinding.inflate(inflater, container, false)
        val root: View = binding.root

        loadUserAttempt()

        return root
    }

    private fun loadUserAttempt() {
        try {
            // Use Safe Args to get the passed argument
            val args: InstructorViewUserAttemptFragmentArgs by navArgs()

            val quiz = quizsForCourseViewModel.quizs.value?.find { quiz ->
                quiz.id == args.quizId
            }

            val attempt = quiz?.finishedUserAttempts?.get(args.userId)

            if (attempt == null) {
                throw InstructorQuizDetailsError("Specified attempt not found")
            }

            binding.textHome.text = attempt.studentId
        } catch (err: Exception) {
            if (err is FirebaseAuthError || err is InstructorQuizError) {
                binding.errorMessage.text = err.message
                return
            }
            throw err
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}