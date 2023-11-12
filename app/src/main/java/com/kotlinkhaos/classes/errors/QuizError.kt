package com.kotlinkhaos.classes.errors;

abstract class QuizError(message: String) : Exception(message)

class QuizCreationError(message: String) : QuizError(message)

class QuizApiError(status: Int, message: String) : QuizError(message)