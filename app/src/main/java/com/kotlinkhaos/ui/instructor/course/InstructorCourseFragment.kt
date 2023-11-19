package com.kotlinkhaos.ui.instructor.course

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.classes.errors.UserError
import com.kotlinkhaos.classes.user.User
import com.kotlinkhaos.classes.utils.loadImage
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

        binding.changeProfilePicture.setOnClickListener {
            handleChangeProfilePicture()
        }
        updateProfilePicture()

        val textView: TextView = binding.textCourse
        textView.text = "This is instructor course Fragment"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateProfilePicture() {
        try {
            val imageUrl = User.getProfilePicture()
            binding.profilePicture.loadImage(imageUrl, binding.profilePictureLoading)
        } catch (err: Exception) {
            if (err is FirebaseAuthError) {
                binding.errorMessage.text = err.message
                return
            }
            throw err
        }
    }

    // Callback for image selection result
    private val pickImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch {
                    try {
                        val selectedImageUri = result.data?.data
                        if (selectedImageUri != null) {
                            User.uploadProfilePicture(requireContext(), selectedImageUri)
                            updateProfilePicture()
                        }
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

    // Callback for permission request result
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openGalleryForImage()
            }
        }

    private fun handleChangeProfilePicture() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                READ_MEDIA_IMAGES
            ) -> {
                openGalleryForImage()
            }

            else -> {
                requestPermissionLauncher.launch(READ_MEDIA_IMAGES)
            }
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageResultLauncher.launch(intent)
    }
}