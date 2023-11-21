package com.kotlinkhaos.classes.course

import com.kotlinkhaos.classes.errors.CourseJoinError
import com.kotlinkhaos.classes.user.User
import com.kotlinkhaos.classes.user.UserDetails


class CourseStudent private constructor() {
    companion object {

        suspend fun joinCourse(courseDetails: CourseDetails, user: User) {
            if (user.getCourseId().isNotEmpty()) {
                throw CourseJoinError("User already enrolled in course")
            }
            courseDetails.studentIds.add(user.getUserId())
            val userDetails = UserDetails(courseDetails.id, user.getName(), user.getType())
            User.createUserDetails(user.getUserId(), userDetails)
            CourseInstructor.createCourseDetails(courseDetails)
        }


    }

}