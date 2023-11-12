package com.kotlinkhaos.classes.practiceQuiz

import com.kotlinkhaos.classes.User
import com.kotlinkhaos.classes.services.KotlinKhaosPracticeQuizApi

class PracticeQuiz private constructor(
    private val id: String,
    private var question: String,
    private var feedback: String,
    private var currentQuestionNumber: Int,
    private var finalScore: Int,
) {
    companion object {
        suspend fun start(prompt: String): PracticeQuiz {
            val token = User.getJwt()
            val kotlinKhaosApi = KotlinKhaosPracticeQuizApi(token)
            val res = kotlinKhaosApi.startPracticeQuiz(prompt)
            return PracticeQuiz(res.practiceQuizId, res.problem, "", 1, 0)
        }
    }

    fun getId(): String {
        return this.id;
    }

    fun getQuestion(): String {
        return this.question;
    }

    fun getFeedback(): String {
        return this.feedback
    }

    fun getCurrentQuestionNumber(): Int {
        return this.currentQuestionNumber;
    }

    fun getFinalScore(): Int {
        return this.finalScore
    }

    private fun setQuestion(question: String) {
        this.question = question
    }

    private fun incrementQuestionNumber() {
        this.currentQuestionNumber++
    }

    private fun setFeedback(feedback: String) {
        this.feedback = feedback
    }

    suspend fun sendAnswer(answer: String) {
        val token = User.getJwt()
        val kotlinKhaosApi = KotlinKhaosPracticeQuizApi(token)
        val res = kotlinKhaosApi.sendPracticeQuizAnswer(this.getId(), answer)
        setFeedback(res.feedback)
    }

    suspend fun continuePracticeQuiz(): Boolean {
        val token = User.getJwt()
        val kotlinKhaosApi = KotlinKhaosPracticeQuizApi(token)
        val res = kotlinKhaosApi.continuePracticeQuiz(this.getId())
        if (res.problem != null) {
            setQuestion(res.problem)
            incrementQuestionNumber()
            return true
        }
        if (res.score != null) {
            finalScore = res.score
        }
        return false
    }
}