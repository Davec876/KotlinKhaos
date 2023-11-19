package com.kotlinkhaos.classes.course

import com.google.firebase.database.FirebaseDatabase
import com.kotlinkhaos.classes.errors.CourseCreationError
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.classes.user.User
import com.kotlinkhaos.classes.user.UserType
import kotlinx.coroutines.tasks.await
import java.util.UUID

enum class EducationLevelType {
    UNIVERSITY_LEVEL,
    ELEMENTARY,
    HIGH_SCHOOL,
    NONE
}

// No-argument constructor, initialized empty strings
private data class CourseDetails(
    val id: String = "",
    val instructorId: String = "",
    val name: String = "",
    val educationLevel: EducationLevelType = EducationLevelType.NONE,
    val description: String = "",
    val studentIds: Set<String> = emptySet(),
    val quizIds: Set<String> = emptySet(),
)

class CourseInstructor private constructor(
    private val id: String,
    private val instructorId: String,
    private val name: String,
    private val educationLevel: EducationLevelType,
    private val description: String,
    private val studentIds: Set<String>,
    private val quizIds: Set<String>,
) {
    companion object {

        private fun validateCourseParameters(courseDetails: CourseDetails) {
            // TO Do: validate enum
            if (courseDetails.name.isEmpty() && courseDetails.description.isEmpty()) {
                throw FirebaseAuthError(" must not be empty")
            }

        }

        private suspend fun createCourseDetails(courseDetails: CourseDetails) {
            try {
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("courses/${courseDetails.id}")
                databaseReference.setValue(courseDetails).await()
            } catch (e: Exception) {
                throw FirebaseAuthError("Failed to create course details: ${e.message}")
            }
        }


        suspend fun create(
            instructor: User,
            name: String,
            educationLevel: EducationLevelType,
            description: String
        ): CourseInstructor {
            if (instructor.getType() != UserType.INSTRUCTOR) {
                throw CourseCreationError("You do not have the authorization level to create a class")
            }
            // When a user is first created, they don't have a courseId
            val courseDetails = CourseDetails(
                id = UUID.randomUUID().toString(),
                instructor.getUserId(),
                name,
                educationLevel,
                description,
                studentIds = emptySet(),
                quizIds = emptySet()
            )
            validateCourseParameters(courseDetails)
            createCourseDetails(courseDetails)
            return CourseInstructor(
                courseDetails.id,
                courseDetails.instructorId,
                courseDetails.name,
                courseDetails.educationLevel,
                courseDetails.description,
                courseDetails.studentIds,
                courseDetails.quizIds
            )

        }


    }

    fun getId(): String {
        return this.id
    }

}