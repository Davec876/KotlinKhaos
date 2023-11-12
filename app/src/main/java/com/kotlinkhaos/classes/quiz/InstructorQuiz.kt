package com.kotlinkhaos.classes.quiz

import com.kotlinkhaos.classes.User
import com.kotlinkhaos.classes.errors.QuizCreationError
import com.kotlinkhaos.classes.services.KotlinKhaosQuizInstructorApi
import com.kotlinkhaos.classes.services.QuizCreateReq

class InstructorQuiz private constructor(
    private val id: String,
    private var name: String,
    private val questionLimit: Int,
    private var questions: MutableList<String>
) {
    companion object {
        private fun validateQuizCreateOptions(quizCreateOptions: QuizCreateReq.Options) {
            if (quizCreateOptions.name.isEmpty() || quizCreateOptions.prompt.isEmpty()) {
                throw QuizCreationError("Name and prompt must not be empty")
            }
        }

        suspend fun createQuiz(
            quizCreateOptions: QuizCreateReq.Options
        ): InstructorQuiz {
            validateQuizCreateOptions(quizCreateOptions)
            val token = User.getJwt()
            val kotlinKhaosApi = KotlinKhaosQuizInstructorApi(token)
            val res = kotlinKhaosApi.createQuiz(quizCreateOptions)
            val questions = mutableListOf(res.firstQuestion)
            return InstructorQuiz(
                res.quizId,
                quizCreateOptions.name,
                quizCreateOptions.questionLimit,
                questions
            )
        }
    }

    fun getQuizId(): String {
        return this.id;
    }

    fun getName(): String {
        return this.name;
    }

    fun getQuestionLimit(): Int {
        return this.questionLimit;
    }

    private fun setQuestions(questions: List<String>) {
        this.questions = questions.toMutableList()
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
        setQuestions(questions)
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