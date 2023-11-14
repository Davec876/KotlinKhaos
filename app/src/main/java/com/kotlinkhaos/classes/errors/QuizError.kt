package com.kotlinkhaos.classes.errors;

abstract class QuizError(message: String) : Exception(message)

class QuizCreationError(message: String) : QuizError(message)

class QuizNetworkError : QuizError("A network error has occurred.")

class QuizApiError(status: Int, message: String) : QuizError(message)