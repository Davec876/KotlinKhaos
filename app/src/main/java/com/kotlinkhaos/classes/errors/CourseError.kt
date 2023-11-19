package com.kotlinkhaos.classes.errors;

abstract class CourseError(message: String) : Exception(message)

class CourseCreationError(message: String) : CourseError(message)


