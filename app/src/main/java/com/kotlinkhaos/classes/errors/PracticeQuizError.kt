package com.kotlinkhaos.classes.errors;

abstract class PracticeQuizError(message: String) : Exception(message)

class PracticeQuizApiError(status: Int, message: String) : PracticeQuizError(message)