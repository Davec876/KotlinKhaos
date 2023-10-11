package com.kotlinkhaos

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kotlinkhaos.classes.User
import com.kotlinkhaos.classes.errors.FirebaseAuthError
import com.kotlinkhaos.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun handleLogin(view: View) {
        lifecycleScope.launch {
            try {
                val email: String = binding.inputEmailAddress.text.toString().trim()
                val password: String = binding.inputPassword.text.toString().trim()
                val user = User.login(email, password)
                if (user != null) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (err: Exception) {
                if (err is FirebaseAuthError) {
                    binding.errorMessage.text = err.message
                    return@launch
                }
                throw err;
            }

        }
    }
}