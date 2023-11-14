package com.kotlinkhaos

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kotlinkhaos.classes.user.User
import com.kotlinkhaos.classes.user.UserType
import com.kotlinkhaos.databinding.ActivityMainBinding
import com.kotlinkhaos.ui.auth.AuthActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setLoading(true)
        lifecycleScope.launch {
            val user = User.getUser()
            // Check if user is signed in (non-null) and update UI accordingly.
            if (user == null) {
                moveToAuthActivity()
                return@launch
            }
            setupUIBasedOnUserType(user.getType())
            setLoading(false)
        }
    }

    private fun setupUIBasedOnUserType(userType: UserType) {
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // If user is an instructor, setup instructor navigation
        if (userType == UserType.INSTRUCTOR) {
            navView.inflateMenu(R.menu.instructor_bottom_nav_menu)
            navController.graph =
                navController.navInflater.inflate(R.navigation.instructor_main_navigation)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_instructor_home, R.id.navigation_instructor_course
                )
            )
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.navigation_instructor_home -> navView.visibility = View.VISIBLE
                    R.id.navigation_instructor_course -> navView.visibility = View.VISIBLE
                    else -> navView.visibility = View.GONE
                }
            }
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        } else {
            // If user is a student, setup student navigation
            navView.inflateMenu(R.menu.student_bottom_nav_menu)
            navController.graph =
                navController.navInflater.inflate(R.navigation.student_main_navigation)
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_student_home,
                    R.id.navigation_student_practice,
                    R.id.navigation_student_profile
                )
            )
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.navigation_student_home -> navView.visibility = View.VISIBLE
                    R.id.navigation_student_practice -> navView.visibility = View.VISIBLE
                    R.id.navigation_student_profile -> navView.visibility = View.VISIBLE
                    else -> navView.visibility = View.GONE
                }
            }
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
    }

    private fun setLoading(loading: Boolean) {
        with(binding) {
            if (loading) {
                loadingMainNavHost.visibility = View.VISIBLE
                navHostFragmentActivityMain.visibility = View.GONE
                navView.visibility = View.GONE
            } else {
                loadingMainNavHost.visibility = View.GONE
                navHostFragmentActivityMain.visibility = View.VISIBLE
                navView.visibility = View.VISIBLE
            }
        }
    }

    private fun moveToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val logoutItem = menu?.findItem(R.id.action_logout)
        logoutItem?.icon?.setTint(
            ContextCompat.getColor(this, R.color.white)
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmDialog() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Log Out") { _, _ ->
                User.logout()
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.red))
    }
}