package com.kotlinkhaos.ui.student.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kotlinkhaos.R
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.classes.errors.StudentQuizError
import com.kotlinkhaos.classes.errors.UserError
import com.kotlinkhaos.classes.quiz.StudentQuizAttempt
import com.kotlinkhaos.classes.services.StudentWeeklySummaryRes
import com.kotlinkhaos.classes.user.User
import com.kotlinkhaos.classes.utils.loadImage
import com.kotlinkhaos.classes.utils.openPictureGallery
import com.kotlinkhaos.classes.utils.setupImagePickerCallbacks
import com.kotlinkhaos.databinding.FragmentStudentProfileBinding
import kotlinx.coroutines.launch

class StudentProfileFragment : Fragment() {
    private var _binding: FragmentStudentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textProfile.text = resources.getString(R.string.greeting)

        // Setup profile picture image picker
        val (imagePicker, requestPermissionLauncher) = setupImagePickerCallbacks { selectedImageUri ->
            if (selectedImageUri != null) {
                uploadImage(selectedImageUri)
            }
        }
        binding.profilePictureLayout.changeProfilePicture.setOnClickListener {
            openPictureGallery(imagePicker, requestPermissionLauncher)
        }
        loadProfilePicture()

        //Call function
        loadWeeklySummary()

        return root
    }

    //Method to format float values for BarChart data as integers
    class IntegerPercentFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "${(value * 10).toInt()}%" //Formats the values as integer percentages
        }
    }

    //Method to calculate the user's average progress over a week
    private fun calculateProgress(weeklySummaryRes: StudentWeeklySummaryRes.WeeklySummary): Boolean {

        val scores = listOf(
            weeklySummaryRes.sun?.averageScore,
            weeklySummaryRes.mon?.averageScore,
            weeklySummaryRes.tues?.averageScore,
            weeklySummaryRes.wed?.averageScore,
            weeklySummaryRes.thurs?.averageScore,
            weeklySummaryRes.fri?.averageScore,
            weeklySummaryRes.sat?.averageScore
        )

        for (i in 0 until scores.size - 1) {
            val current = scores[i]
            val next = scores[i + 1]
            if (current == null || next == null || current >= next) {
                return false
            }
        }

        return true
    }

    private fun loadWeeklySummary() {
        setLoadingState(true)
        lifecycleScope.launch {
            try {
                //Initialize barchart, class variable
                val barChart = binding.barChart
                val weeklySummaryRes = StudentQuizAttempt.getWeeklySummaryForStudent()
                val entries = mutableListOf<BarEntry>()
                val weekDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                //Calculates their progress
                val hasProgress = calculateProgress(weeklySummaryRes)

                for ((index, _) in weekDays.withIndex()) {
                    val daySummary = when (index) {
                        0 -> weeklySummaryRes.sun
                        1 -> weeklySummaryRes.mon
                        2 -> weeklySummaryRes.tues
                        3 -> weeklySummaryRes.wed
                        4 -> weeklySummaryRes.thurs
                        5 -> weeklySummaryRes.fri
                        6 -> weeklySummaryRes.sat
                        else -> null
                    }

                    val averageScore = daySummary?.averageScore ?: 0f
                    entries.add(
                        BarEntry(
                            index.toFloat(),
                            averageScore
                        )
                    )
                }

                //Configuration of the BarChart
                val barDataSet = BarDataSet(entries, "Daily Average Quiz Score")
                val barData = BarData(barDataSet)
                barDataSet.color = ContextCompat.getColor(requireContext(), R.color.purple_500)
                barChart.data = barData
                barChart.xAxis.valueFormatter = IndexAxisValueFormatter(weekDays)
                barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                barChart.xAxis.granularity = 1f

                //Disables the grid lines for both X and Y axes(background)
                barChart.axisLeft.setDrawGridLines(false)
                barChart.axisRight.setDrawGridLines(false)
                barChart.xAxis.setDrawGridLines(false)

                //Disables the left y-axis view
                barChart.axisLeft.isEnabled = false
                //Sets the y-axes to start from 0
                barChart.axisLeft.axisMinimum = 0f
                barChart.axisRight.axisMinimum = 0f
                barChart.axisRight.valueFormatter = IntegerPercentFormatter()

                barChart.description.text = ""
                barChart.invalidate()

                //Checks their progress to show any compliment
                if (hasProgress) {
                    showCompliment()
                }

            } catch (err: Exception) {
                if (err is FirebaseAuthError || err is StudentQuizError) {
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
        binding.loadingBarChart.visibility = if (loading) View.VISIBLE else View.GONE
        binding.barChart.visibility = if (loading) View.GONE else View.VISIBLE
    }

    private fun showCompliment() {
        binding.textProfile.text = resources.getString(R.string.compliment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}