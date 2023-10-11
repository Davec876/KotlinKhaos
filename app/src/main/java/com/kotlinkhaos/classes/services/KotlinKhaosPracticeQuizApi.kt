package com.kotlinkhaos.classes.services;

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class KotlinKhaosPracticeQuizApi(private val token: String) {
    private val apiHost = "https://kotlin-khaos-api.maximoguk.com"
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
            })
        }
    }

    suspend fun startPracticeQuiz(prompt: String): PracticeQuizStartRes {
        try {
            val parsedRes: PracticeQuizStartRes = client.post("$apiHost/practice-quizs") {
                parameter("prompt", prompt)
                addRequiredApiHeaders()
            }.body()
            return parsedRes
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in startPracticeQuiz", err)
            throw err
        } finally {
            client.close()
        }
    }

    suspend fun getPracticeQuizById(
        practiceQuizId: String
    ): PracticeQuizGetByIdRes {
        try {
            val parsedRes: PracticeQuizGetByIdRes =
                client.get("$apiHost/practice-quizs/$practiceQuizId") {
                }.body()
            return parsedRes
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in getPractice", err)
            throw err
        } finally {
            client.close()
        }
    }

    suspend fun sendPracticeQuizAnswer(
        practiceQuizId: String,
        answer: String
    ): PracticeQuizAnswerRes {
        try {
            val parsedRes: PracticeQuizAnswerRes =
                client.post("$apiHost/practice-quizs/$practiceQuizId") {
                    setBody(PracticeQuizAnswerReq(answer))
                    addRequiredApiHeaders()
                }.body()
            return parsedRes
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in sendPracticeQuizAnswer", err)
            throw err
        } finally {
            client.close()
        }
    }

    suspend fun continuePracticeQuiz(
        practiceQuizId: String
    ): PracticeQuizContinueRes {
        try {
            val parsedRes: PracticeQuizContinueRes =
                client.post("$apiHost/practice-quizs/$practiceQuizId/continue") {
                    addRequiredApiHeaders()
                }.body()
            return parsedRes
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in continuePracticeQuiz", err)
            throw err
        } finally {
            client.close()
        }
    }

    private fun HttpRequestBuilder.addRequiredApiHeaders() {
        header(HttpHeaders.Authorization, "Bearer $token")
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
}

@Serializable
data class PracticeQuizStartRes(val problem: String, val practiceQuizId: String)

@Serializable
data class PracticeQuizGetByIdRes(val message: String? = null, val score: Int? = null)

@Serializable
data class PracticeQuizAnswerReq(val answer: String)

@Serializable
data class PracticeQuizAnswerRes(val feedback: String)

@Serializable
data class PracticeQuizContinueRes(val problem: String? = null, val score: Int? = null)