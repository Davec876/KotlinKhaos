package com.kotlinkhaos.ui.instructor.course

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.classes.errors.UserError
import com.kotlinkhaos.classes.user.User
import com.kotlinkhaos.classes.utils.loadImage
import com.kotlinkhaos.classes.utils.openPictureGallery
import com.kotlinkhaos.classes.utils.setupImagePickerCallbacks
import com.kotlinkhaos.databinding.FragmentInstructorCourseBinding
import kotlinx.coroutines.launch

class InstructorCourseFragment : Fragment() {
    private var _binding: FragmentInstructorCourseBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstructorCourseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val (imagePicker, requestPermissionLauncher) = setupImagePickerCallbacks { selectedImageUri ->
            if (selectedImageUri != null) {
                uploadImage(selectedImageUri)
            }
        }
        binding.profilePictureLayout.changeProfilePicture.setOnClickListener {
            openPictureGallery(imagePicker, requestPermissionLauncher)
        }
        loadProfilePicture()

        val textView: TextView = binding.textCourse
        textView.text = "This is instructor course Fragment"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadProfilePicture() {
        try {
            val imageUrl = User.getProfilePicture()
            binding.profilePictureLayout.profilePicture.loadImage(
                imageUrl,
                binding.profilePictureLayout.profilePictureLoading
            )
        } catch (err: Exception) {
            if (err is FirebaseAuthError) {
                binding.errorMessage.text = err.message
                return
            }
            throw err
        }
    }

    private fun uploadImage(selectedImageUri: Uri) {
        lifecycleScope.launch {
            try {
                User.uploadProfilePicture(requireContext(), selectedImageUri)
                loadProfilePicture()
            } catch (err: Exception) {
                if (err is FirebaseAuthError || err is UserError) {
                    binding.errorMessage.text = err.message
                    return@launch
                }
                throw err
            }
        }
    }
}