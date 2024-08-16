package com.student.competishun.ui.main

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.student.competishun.R
import com.student.competishun.data.model.UpdateUserInput
import com.student.competishun.databinding.ActivityMainBinding
import com.student.competishun.ui.viewmodel.MainVM
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import com.apollographql.apollo3.api.Optional

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainVM: MainVM by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    lateinit var userInput: UpdateUserInput

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Initialize SharedPreferencesManager
        sharedPreferencesManager = SharedPreferencesManager(this)
        userInput = UpdateUserInput()

        // Show splash and welcome screens
        showSplashAndWelcomeScreens()
    }

    private fun showSplashAndWelcomeScreens() {
        setContentView(R.layout.splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            // Switch to the welcome screen after a delay
            setContentView(R.layout.welcome_screen)

            Handler(Looper.getMainLooper()).postDelayed({


                // Switch to the main layout after a delay
                binding = ActivityMainBinding.inflate(layoutInflater)
                setContentView(binding.root)
                enableEdgeToEdge()

                ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                    )
                    insets
                }

                setupNavigation()
                getUserInfo()
            }, 2000)
        }, 2000)
    }

    private fun getUserInfo() {
        // Check user data and navigate accordingly

        when {
            !sharedPreferencesManager.reference.isNullOrEmpty() -> {
                Log.e(
                    "saved ref",
                    sharedPreferencesManager.reference.toString() + userInput.fullName
                )
                navigateToRefFragment()
            }

            !sharedPreferencesManager.preparingFor.isNullOrEmpty() -> {
                Log.e(
                    "saved prepare",
                    sharedPreferencesManager.preparingFor.toString() + userInput.fullName
                )
                navigateToTargetFragment()
            }
            sharedPreferencesManager.targetYear != 0 -> {
                Log.e("saved target", sharedPreferencesManager.targetYear.toString())
                navigateToRefFragment()
            }
            !sharedPreferencesManager.name.isNullOrEmpty() && !sharedPreferencesManager.city.isNullOrEmpty() -> {
                navigateToPreparationFragment()
            }

            else -> {
                // navigateToWelcomeFragment()
            }
        }
    }

    private fun navigateToPreparationFragment() {
        navController.navigate(R.id.PrepForFragment)
    }

    private fun navigateToTargetFragment() {
        navController.navigate(R.id.TargetFragment)
    }

    private fun navigateToRefFragment() {
        navController.navigate(R.id.ReferenceFragment)
    }

    private fun navigateToWelcomeFragment() {
        // This could be used if you need a dedicated welcome fragment
        navController.navigate(R.id.onWelcomeFragment)
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        navController =
            navHostFragment?.navController ?: throw IllegalStateException("NavController not found")
        Log.d("MainActivity", "NavController: $navController")
    }
    private fun checkToken() {
        val token = sharedPreferencesManager.accessToken
        val number = sharedPreferencesManager.updateUserInput
        Log.e("checktoken", token.toString())
        Log.e("number  ${number?.fullName}", number?.city.toString())
        if (token != null) {
            // Token is available, navigate to the main screen
            navigateToMainScreen()
        } else {
            // Token is not available, navigate to login or other appropriate screen
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
        }
    }

    private fun navigateToMainScreen() {
        // Navigate to the main activity
        startActivity(Intent(this, HomeActivity::class.java))
        finish() // Close the splash activity
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
