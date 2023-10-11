package com.kotlinkhaos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kotlinkhaos.classes.User
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun handleLogin(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val emailView = findViewById<EditText>(R.id.inputEmailAddress)
                val passwordView = findViewById<EditText>(R.id.inputPassword)
                val email: String = emailView.text.toString().trim()
                val password: String = passwordView.text.toString().trim()
                val user = User.login(email, password)
                if (user != null) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            } catch (err: Exception) {
                if (err is FirebaseAuthError) {
                    val errorMessageView = findViewById<TextView>(R.id.errorMessage)
                    errorMessageView.text = err.message
                    return@launch
                }
                throw err;
            }

        }
    }
}