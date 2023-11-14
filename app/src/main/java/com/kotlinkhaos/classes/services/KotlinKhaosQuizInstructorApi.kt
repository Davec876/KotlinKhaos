package com.kotlinkhaos.classes.services;

import android.util.Log
import com.kotlinkhaos.classes.errors.KotlinKhaosApiError
import com.kotlinkhaos.classes.errors.QuizApiError
import com.kotlinkhaos.classes.errors.QuizNetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.channels.UnresolvedAddressException

class KotlinKhaosQuizInstructorApi(private val token: String) {
    private val apiHost = "https://kotlin-khaos-api.maximoguk.com"
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
            })
        }
    }

    suspend fun createQuiz(quizCreateOptions: QuizCreateReq.Options): QuizCreateRes {
        try {
            val res = client.post("$apiHost/instructor/quizs") {
                setBody(QuizCreateReq(quizCreateOptions))
                addRequiredApiHeaders()
            }
            return parseResponseFromApi(res)
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in startQuiz", err)
            if (err is UnresolvedAddressException) {
                throw QuizNetworkError()
            }
            throw err
        } finally {
            client.close()
        }
    }

    suspend fun nextQuestion(quizId: String): QuizNextQuestionRes {
        try {
            val res =
                client.post("$apiHost/instructor/quizs/$quizId/next-question") {
                    addRequiredApiHeaders()
                }
            return parseResponseFromApi(res)
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in nextQuestion", err)
            if (err is UnresolvedAddressException) {
                throw QuizNetworkError()
            }
            throw err
        } finally {
            client.close()
        }
    }

    suspend fun editQuestions(quizId: String, questions: List<String>): QuizEditQuestionRes {
        try {
            val res =
                client.put("$apiHost/instructor/quizs/$quizId/edit") {
                    setBody(QuizEditQuestionReq(questions))
                    addRequiredApiHeaders()
                }
            return parseResponseFromApi(res)
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in editQuestions", err)
            if (err is UnresolvedAddressException) {
                throw QuizNetworkError()
            }
            throw err
        } finally {
            client.close()
        }
    }

    suspend fun startQuiz(quizId: String): QuizStartRes {
        try {
            val res = client.post("$apiHost/instructor/quizs/$quizId/start") {
                addRequiredApiHeaders()
            }
            return parseResponseFromApi(res)
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in startQuiz", err)
            if (err is UnresolvedAddressException) {
                throw QuizNetworkError()
            }
            throw err
        } finally {
            client.close()
        }
    }

    suspend fun finishQuiz(quizId: String): QuizFinishRes {
        try {
            val res = client.post("$apiHost/instructor/quizs/$quizId/finish") {
                addRequiredApiHeaders()
            }
            return parseResponseFromApi(res)
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in finishQuiz", err)
            if (err is UnresolvedAddressException) {
                throw QuizNetworkError()
            }
            throw err
        } finally {
            client.close()
        }
    }

    suspend fun getQuizsForCourse(): QuizsForCourseRes {
        try {
            val res = client.get("$apiHost/instructor/course/quizs") {
                addRequiredApiHeaders()
            }
            return parseResponseFromApi(res)
        } catch (err: Exception) {
            Log.e("KotlinKhaosApi", "Error in getQuizsForACourse", err)
            if (err is UnresolvedAddressException) {
                throw QuizNetworkError()
            }
            throw err
        } finally {
            client.close()
        }
    }

    private fun HttpRequestBuilder.addRequiredApiHeaders() {
        header(HttpHeaders.Authorization, "Bearer $token")
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }

    private suspend inline fun <reified T> parseResponseFromApi(res: HttpResponse): T {
        if (res.status.isSuccess()) {
            return res.body()
        }
        val apiError: KotlinKhaosApiError = res.body()
        throw QuizApiError(apiError.status, apiError.error)
    }
}

@Serializable
data class QuizCreateReq(
    val options: Options
) {
    @Serializable
    data class Options(
        val name: String,
        val questionLimit: Int,
        val prompt: String
    )
}

@Serializable
data class QuizCreateRes(val quizId: String, val firstQuestion: String)

@Serializable
data class QuizNextQuestionRes(val question: String)

@Serializable
data class QuizEditQuestionReq(val questions: List<String>)

@Serializable
data class QuizEditQuestionRes(val success: Boolean)

@Serializable
data class QuizStartRes(val success: Boolean)

@Serializable
data class QuizFinishRes(val success: Boolean)

@Serializable
data class QuizsForCourseRes(
    val quizs: List<QuizDetailsRes>
) {
    @Serializable
    data class QuizDetailsRes(
        val id: String,
        val name: String,
        val started: Boolean,
        val finished: Boolean,
        val questions: List<Question>,
        val startedAttemptsUserIds: List<String>,
        val finishedUserAttempts: Map<String, UserAttempt>
    ) {
        @Serializable
        data class Question(
            val content: String,
            val role: String
        )

        @Serializable
        data class UserAttempt(
            val attemptId: String,
            val studentId: String,
            val score: Int,
            val submittedOn: Instant
        )
    }
}