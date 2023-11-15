package com.kotlinkhaos.ui.instructor.home

import SpaceItemDecorationHeight
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.classes.errors.InstructorQuizError
import com.kotlinkhaos.classes.quiz.InstructorQuiz
import com.kotlinkhaos.databinding.FragmentInstructorHomeBinding
import kotlinx.coroutines.launch

class InstructorHomeFragment : Fragment() {
    private var _binding: FragmentInstructorHomeBinding? = null
    private lateinit var quizsForCourseListAdapter: QuizsForCourseListAdapter

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
        binding.addQuizFAB.setOnClickListener {
            handleAddQuiz()
        }
        binding.refreshQuizForCourseList.setOnRefreshListener {
            loadQuizListForCourse()
        }
        quizsForCourseListAdapter = QuizsForCourseListAdapter(emptyList())
        binding.quizsForCourseList.adapter = quizsForCourseListAdapter
        binding.quizsForCourseList.layoutManager = LinearLayoutManager(requireContext())
        binding.quizsForCourseList.addItemDecoration(
            SpaceItemDecorationHeight(
                4
            )
        )

        loadQuizListForCourse()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadQuizListForCourse() {
        setLoadingState(true)
        lifecycleScope.launch {
            try {
                val quizs = InstructorQuiz.getQuizsForCourse()
                quizsForCourseListAdapter.updateData(quizs)
                binding.quizsForCourseList.setHasFixedSize(true) // fixed list performance optimization
            } catch (err: Exception) {
                if (err is FirebaseAuthError || err is InstructorQuizError) {
                    binding.errorMessage.text = err.message
                    return@launch
                }
                throw err
            } finally {
                setLoadingState(false)
            }
        }
    }

    private fun setLoadingState(loading: Boolean) {
        binding.refreshQuizForCourseList.post {
            binding.refreshQuizForCourseList.isRefreshing = loading
        }
    }

    private fun handleAddQuiz() {
        val action = InstructorHomeFragmentDirections.startNavigationCreateQuiz()
        findNavController().navigate(action)
    }
}