package com.kotlinkhaos.classes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class User private constructor(private val fireBaseUser: FirebaseUser) {
    companion object {
        suspend fun login(email: String, pass: String): User? {
            val mAuth = FirebaseAuth.getInstance()
            val result = mAuth.signInWithEmailAndPassword(email, pass).await() ?: return null
            return User(result.user!!)
        }

        fun getUser(): User? {
            val mAuth = FirebaseAuth.getInstance()
            val loadedFirebaseUser = mAuth.currentUser ?: return null
            loadedFirebaseUser.reload()
            return User(loadedFirebaseUser)
        }
    }

    suspend fun getJwt(): String {
        return this.fireBaseUser.getIdToken(true).await().token
            ?: throw Exception("Error getting token!")
    }
}