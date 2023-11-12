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
            quizStartOptions: QuizCreateReq.Options
        ): InstructorQuiz {
            val token = User.getJwt()
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

    suspend fun nextQuestion() {
        val token = User.getJwt()
        val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
        val res = kotlinKhaosApi.nextQuestion(this.getQuizId())
        appendQuestion(res.question)
    }

    suspend fun editQuestions(questions: List<String>) {
        val token = User.getJwt()
        val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
        kotlinKhaosApi.editQuestions(this.getQuizId(), questions)
    }

    suspend fun start() {
        val token = User.getJwt()
        val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
        kotlinKhaosApi.startQuiz(this.getQuizId())
    }

    suspend fun finish() {
        val token = User.getJwt()
        val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
        kotlinKhaosApi.finishQuiz(this.getQuizId())
    }
}