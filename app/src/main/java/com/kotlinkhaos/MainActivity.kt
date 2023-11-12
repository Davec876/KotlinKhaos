package com.kotlinkhaos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kotlinkhaos.classes.User
import com.kotlinkhaos.classes.UserType
import com.kotlinkhaos.databinding.ActivityMainBinding
import com.kotlinkhaos.ui.auth.AuthActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Set layout as loading until user is fetched
        setContentView(R.layout.loading_layout)
        // Check if user is signed in (non-null) and update UI accordingly.
        lifecycleScope.launch {
            val user = User.getUser()
            if (user == null) {
                moveToAuthActivity()
                Log.i("Firebase", "User is not logged in!")
                return@launch
            }
            val navView: BottomNavigationView = binding.navView
            // If user is an instructor, clear default nav and setup instructor navigation
            if (user.getType() == UserType.INSTRUCTOR) {
                setContentView(binding.root)
                navView.menu.clear()
                navView.inflateMenu(R.menu.instructor_bottom_nav_menu)

                val navController = findNavController(R.id.nav_host_fragment_activity_main)
                navController.graph =
                    navController.navInflater.inflate(R.navigation.instructor_main_navigation)
                val appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.navigation_instructor_home, R.id.navigation_instructor_course
                    )
                )
                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)
                return@launch
            }

            setContentView(binding.root)
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_student_home,
                    R.id.navigation_student_practice,
                    R.id.navigation_student_profile
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
    }

    private fun moveToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}