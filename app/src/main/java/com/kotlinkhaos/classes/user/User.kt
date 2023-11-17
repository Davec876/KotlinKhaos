package com.kotlinkhaos.classes.user

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import kotlinx.coroutines.tasks.await

enum class UserType {
    STUDENT,
    INSTRUCTOR,
    NONE
}

// No-argument constructor, initialized empty strings
private data class UserDetails(
    val courseId: String = "",
    val name: String = "",
    val type: UserType = UserType.NONE
)

class User private constructor(
    private val userId: String,
    private val courseId: String,
    private val name: String,
    private val type: UserType
) {
    companion object {
        private fun validateLoginParameters(email: String, pass: String) {
            if (email.isEmpty() || pass.isEmpty()) {
                throw FirebaseAuthError("Email and password must not be empty")
            }
        }

        private fun validateRegisterParameters(
            email: String,
            pass: String,
            userDetails: UserDetails
        ) {
            validateLoginParameters(email, pass)
            if (userDetails.name.isEmpty()) {
                throw FirebaseAuthError("Name must not be empty")
            }
        }

        private suspend fun fetchUserDetails(userId: String): UserDetails {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId")
            val dataSnapshot = databaseReference.get().await()
            return dataSnapshot.getValue<UserDetails>()
                ?: throw Exception("User details not found")
        }

        private suspend fun createUserDetails(userId: String, userDetails: UserDetails) {
            try {
                val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId")
                databaseReference.setValue(userDetails).await()
            } catch (e: Exception) {
                throw FirebaseAuthError("Failed to create user details: ${e.message}")
            }
        }

        suspend fun login(email: String, pass: String): User? {
            try {
                validateLoginParameters(email, pass)
                val mAuth = FirebaseAuth.getInstance()
                val result = mAuth.signInWithEmailAndPassword(email, pass).await() ?: return null
                val userDetails = fetchUserDetails(result.user!!.uid)
                return User(
                    result.user!!.uid,
                    userDetails.courseId,
                    userDetails.name,
                    userDetails.type
                )
            } catch (err: Exception) {
                if (err is FirebaseAuthInvalidCredentialsException && err.message != null) {
                    throw FirebaseAuthError(err.message!!)
                }
                if (err is FirebaseException) {
                    if (err.message == "An internal error has occurred. [ INVALID_LOGIN_CREDENTIALS ]") {
                        throw FirebaseAuthError("Invalid password")
                    }
                    throw FirebaseAuthError("An error occurred when logging in")
                }
                throw err
            }
        }

        suspend fun register(email: String, pass: String, name: String, type: UserType): User? {
            // When a user is first created, they don't have a courseId
            val userDetails = UserDetails(courseId = "", name, type)
            validateRegisterParameters(email, pass, userDetails)
            val mAuth = FirebaseAuth.getInstance()
            val result = mAuth.createUserWithEmailAndPassword(email, pass).await() ?: return null
            createUserDetails(result.user!!.uid, userDetails)
            return User(result.user!!.uid, userDetails.courseId, userDetails.name, userDetails.type)
        }

        suspend fun sendForgotPasswordEmail(email: String) {
            if (email.isEmpty()) {
                throw FirebaseAuthError("Email must not be empty")
            }
            val mAuth = FirebaseAuth.getInstance()
            mAuth.sendPasswordResetEmail(email).await()
        }


        suspend fun getUser(): User? {
            try {
                val mAuth = FirebaseAuth.getInstance()
                val loadedFirebaseUser = mAuth.currentUser ?: return null
                loadedFirebaseUser.reload()
                val userDetails = fetchUserDetails(loadedFirebaseUser.uid)
                return User(
                    loadedFirebaseUser.uid,
                    userDetails.courseId,
                    userDetails.name,
                    userDetails.type
                )
            } catch (err: Exception) {
                if (err is FirebaseAuthInvalidUserException) {
                    if (err.message != null) {
                        Log.i("Firebase", err.message!!)
                    }
                    return null
                }
                throw err;
            }
        }

        suspend fun getJwt(): String {
            // Since we're using the firebase sdk, it should manage token refreshes automatically for us
            val mAuth = FirebaseAuth.getInstance()
            val loadedFirebaseUser =
                mAuth.currentUser ?: throw FirebaseAuthError("User is not logged in!")
            return loadedFirebaseUser.getIdToken(false).await().token
                ?: throw Exception("Error getting token!")
        }

        fun logout() {
            FirebaseAuth.getInstance().signOut()
        }
    }

    fun getUserId(): String {
        return this.userId
    }

    fun getCourseId(): String {
        return this.courseId;
    }

    fun getName(): String {
        return this.name
    }

    fun getType(): UserType {
        return this.type
    }
}