package com.kotlinkhaos.classes.quiz

import com.kotlinkhaos.classes.User
import com.kotlinkhaos.classes.services.KotlinKhaosQuizInstructorApi
import com.kotlinkhaos.classes.services.QuizCreateReq

class InstructorQuiz private constructor(
    private val id: String,
    private var name: String,
    private val questions: MutableList<String>
) {
    companion object {
        suspend fun createQuiz(
            user: User,
            quizStartOptions: QuizCreateReq.Options
        ): InstructorQuiz {
            val token = user.getJwt()
            val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
            val res = kotlinKhaosApi.createQuiz(quizStartOptions)
            val questions = mutableListOf(res.firstQuestion)
            return InstructorQuiz(res.quizId, quizStartOptions.name, questions)
        }
    }

    fun getQuizId(): String {
        return this.id;
    }

    fun getName(): String {
        return this.name;
    }

    fun getQuestions(): List<String> {
        return this.questions;
    }

    private fun appendQuestion(question: String) {
        this.questions.add(question)
    }

    suspend fun nextQuestion(user: User) {
        val token = user.getJwt()
        val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
        val res = kotlinKhaosApi.nextQuestion(this.getQuizId())
        appendQuestion(res.question)
    }

    suspend fun editQuestions(user: User, questions: List<String>) {
        val token = user.getJwt()
        val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
        kotlinKhaosApi.editQuestions(this.getQuizId(), questions)
    }

    suspend fun start(user: User) {
        val token = user.getJwt()
        val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
        kotlinKhaosApi.startQuiz(this.getQuizId())
    }

    suspend fun finish(user: User) {
        val token = user.getJwt()
        val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
        kotlinKhaosApi.finishQuiz(this.getQuizId())
    }
}