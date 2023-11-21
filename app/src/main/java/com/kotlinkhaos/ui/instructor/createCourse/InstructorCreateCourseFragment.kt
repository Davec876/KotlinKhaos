package com.kotlinkhaos.ui.instructor.createCourse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.kotlinkhaos.classes.course.EducationLevelType
import com.kotlinkhaos.classes.errors.CourseError
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.classes.user.User
import com.kotlinkhaos.databinding.FragmentInstructorCreateCourseBinding
import kotlinx.coroutines.launch


class InstructorCreateCourseFragment : Fragment() {
    private var _binding: FragmentInstructorCreateCourseBinding? = null
    private val courseCreationViewModel: CourseCreationViewModel by activityViewModels()
    private lateinit var educationlevelListAdapter: ArrayAdapter<*>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstructorCreateCourseBinding.inflate(inflater, container, false)
        val root: View = binding.root


        // Ask if we need this
        val textView: TextView = binding.createClassTextView
        val editTextName: TextInputEditText = binding.inputCourseName
        val editTextDesc: TextInputEditText = binding.inputCourseDesc

        val items = arrayOf(
            EducationLevelType.HIGH_SCHOOL,
            EducationLevelType.ELEMENTARY,
            EducationLevelType.UNIVERSITY_LEVEL
        )

        educationlevelListAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items.map { it.name }
        )
        binding.itemOptions.setAdapter(educationlevelListAdapter)
        val educationLevel: EducationLevelType = EducationLevelType.UNIVERSITY_LEVEL
        // TODO: fix adapter
        binding.itemOptions.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val educationLevel = parent.getItemAtPosition(position)
            print(educationLevel)
            // Now you have the selected option, you can do whatever you want with it
            // For example, you can log it or use it in further processing
        })

        val button: Button = binding.createCourseButton
        button.setOnClickListener {
            this.handleCreateCourse(educationLevel)
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleCreateCourse(educationLevelSelected: EducationLevelType) {
        lifecycleScope.launch {
            try {
                val user = User.getUser()
                //setLoadingState(true)
                val courseName = binding.inputCourseName.text.toString().trim()
                val courseDesc = binding.inputCourseDesc.text.toString().trim()
                // TODO: figure out how to select from menu
                val educationLevel = educationLevelSelected

                if (user != null) {
                    courseCreationViewModel.createNewCourse(
                        user,
                        courseName,
                        courseDesc,
                        educationLevel
                    )
                    val action =
                        InstructorCreateCourseFragmentDirections.startNavigationGoBackToInstructorHome()
                    findNavController().navigate(action)
                }
            } catch (e: Exception) {
                if (e is FirebaseAuthError || e is CourseError) {
                    binding.errorMessage.text = e.message
                    return@launch
                }
                throw e
            }
        }

    }

    private fun setLoadingState(loading: Boolean) {
        binding.courseCreationProgess.visibility = if (loading) View.VISIBLE else View.GONE
        binding.createCourseButton.isEnabled = !loading
    }

}