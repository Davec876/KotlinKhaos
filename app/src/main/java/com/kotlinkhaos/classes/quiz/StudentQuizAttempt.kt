package com.kotlinkhaos.classes.quiz

import com.google.firebase.FirebaseNetworkException
import com.kotlinkhaos.classes.errors.StudentQuizNetworkError
import com.kotlinkhaos.classes.services.KotlinKhaosQuizStudentApi
import com.kotlinkhaos.classes.services.StudentQuizsForCourseRes
import com.kotlinkhaos.classes.services.StudentWeeklySummaryRes
import com.kotlinkhaos.classes.user.User

class StudentQuizAttempt private constructor(
    private val id: String,
    private val quizName: String,
    private val questions: List<String>,
    private var finalScore: Int?,
) {
    companion object {
        suspend fun createQuizAttempt(quizId: String): StudentQuizAttempt {
            try {
                val token = User.getJwt()
                val kotlinKhaosApi = KotlinKhaosQuizStudentApi(token)
                val res = kotlinKhaosApi.createStudentQuizAttempt(quizId)
                val questions = res.questions
                return StudentQuizAttempt(
                    res.quizAttemptId,
                    res.quizName,
                    questions,
                    finalScore = null
                )
            } catch (err: Exception) {
                if (err is FirebaseNetworkException) {
                    throw StudentQuizNetworkError()
                }
                throw err
            }
        }

        suspend fun getQuizsForCourse(): List<StudentQuizsForCourseRes.StudentQuizDetailsRes> {
            try {
                val kotlinKhaosApi = KotlinKhaosQuizStudentApi(User.getJwt())
                return kotlinKhaosApi.getCourseQuizsForStudent().quizs
            } catch (err: Exception) {
                if (err is FirebaseNetworkException) {
                    throw StudentQuizNetworkError()
                }
                throw err
            }
        }

        suspend fun getWeeklySummaryForStudent(): StudentWeeklySummaryRes.WeeklySummary {
            try {
                val kotlinKhaosApi = KotlinKhaosQuizStudentApi(User.getJwt())
                return kotlinKhaosApi.getWeeklySummaryForStudent().weeklySummary
            } catch (err: Exception) {
                if (err is FirebaseNetworkException) {
                    throw StudentQuizNetworkError()
                }
                throw err
            }
        }
    }

    suspend fun submitAttempt(answers: List<String>) {
        try {
            val token = User.getJwt()
            val kotlinKhaosApi = KotlinKhaosQuizStudentApi(token)
            val res = kotlinKhaosApi.submitStudentQuizAttempt(this.getQuizId(), answers)
            setFinalScore(res.score)
        } catch (err: Exception) {
            if (err is FirebaseNetworkException) {
                throw StudentQuizNetworkError()
            }
            throw err
        }
    }

    fun getQuizId(): String {
        return this.id;
    }

    fun getQuizName(): String {
        return this.quizName;
    }

    fun getNumberOfQuestions(): Int {
        return this.questions.size;
    }

    fun getQuestions(): List<String> {
        return this.questions;
    }

    private fun setFinalScore(finalScore: Int) {
        this.finalScore = finalScore
    }

    fun getFinalScore(): Int? {
        return this.finalScore
    }
}