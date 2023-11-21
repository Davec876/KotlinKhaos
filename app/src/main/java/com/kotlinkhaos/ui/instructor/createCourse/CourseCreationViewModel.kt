package com.kotlinkhaos.ui.instructor.createCourse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlinkhaos.classes.course.CourseInstructor
import com.kotlinkhaos.classes.course.EducationLevelType
import com.kotlinkhaos.classes.user.User
import kotlinx.coroutines.launch

class CourseCreationViewModel : ViewModel() {
    private val _courseData = MutableLiveData<CourseInstructor>()
    val courseData: LiveData<CourseInstructor?> = _courseData

    fun createNewCourse(
        user: User,
        courseName: String,
        courseDesc: String,
        educationLevel: EducationLevelType
    ) {
        viewModelScope.launch {
            // TODO: figure out how to pass the data to the create method
            val newCourse = CourseInstructor.create(user, courseName, educationLevel, courseDesc)
            _courseData.value = newCourse
        }
    }

}
