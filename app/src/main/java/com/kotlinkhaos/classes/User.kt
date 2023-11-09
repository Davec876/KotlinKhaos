package com.kotlinkhaos.classes

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import kotlinx.coroutines.tasks.await

class User private constructor(private val fireBaseUser: FirebaseUser) {
    companion object {
        private fun validateLoginParameters(email: String, pass: String) {
            if (email.isEmpty() || pass.isEmpty()) {
                throw FirebaseAuthError("Email and password must not be empty")
            }
        }

        suspend fun login(email: String, pass: String): User? {
            try {
                validateLoginParameters(email, pass)
                val mAuth = FirebaseAuth.getInstance()
                val result = mAuth.signInWithEmailAndPassword(email, pass).await() ?: return null
                return User(result.user!!)
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

        fun getUser(): User? {
            try {
                val mAuth = FirebaseAuth.getInstance()
                val loadedFirebaseUser = mAuth.currentUser ?: return null
                loadedFirebaseUser.reload()
                return User(loadedFirebaseUser)
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

        fun logout() {
            FirebaseAuth.getInstance().signOut()
        }
    }

    suspend fun getJwt(): String {
        // Since we're using the firebase sdk, it should manage token refreshes automatically for us
        return this.fireBaseUser.getIdToken(false).await().token
            ?: throw Exception("Error getting token!")
    }
}