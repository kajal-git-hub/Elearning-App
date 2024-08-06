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
import androidx.navigation.findNavController
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
    private lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    lateinit var userInput: UpdateUserInput
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Initialize SharedPreferencesManager
        sharedPreferencesManager = SharedPreferencesManager(this)
        userInput = UpdateUserInput()
        // Check if the access token is available
        Log.e("accessToken", sharedPreferencesManager.accessToken.toString())
        if (sharedPreferencesManager.accessToken != null) {
            showSplashAndWelcomeScreens()
        } else {
            showSplashAndWelcomeScreens()
        }

        lifecycleScope.launch {
            supervisorScope {
                launch {
                    // Optional coroutine for additional tasks
                }
                launch {
                    mainVM.loader.collect { isLoading ->
                        if (isLoading) {
                            // Handle loading state
                        } else {
                            // Handle when loading is complete
                        }
                    }
                }
            }
        }
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
        Log.e("saved pref", sharedPreferencesManager.preparingFor.toString())
        if (!sharedPreferencesManager.name.isNullOrEmpty() && !sharedPreferencesManager.city.isNullOrEmpty()) {
            navigateToPreparationFragment()
        }
        if (!sharedPreferencesManager.preparingFor.isNullOrEmpty()){
            Log.e("saved pref", sharedPreferencesManager.preparingFor.toString() + userInput.fullName)
            navigateToTargetFragment()
        }
        if (!sharedPreferencesManager.reference.isNullOrEmpty()){
            Log.e("saved pref", sharedPreferencesManager.preparingFor.toString() + userInput.fullName)
            navigateToRefFragment()
        }

        Log.e("saved name", sharedPreferencesManager.name.toString() + userInput.fullName)
    }

    private fun navigateToPreparationFragment() {
        // Ensure the NavController is properly initialized
        if (::navController.isInitialized) {
            navController.navigate(R.id.PrepForFragment)
        } else {
            Log.e("MainActivity", "NavController is not initialized")
        }
    }

    private fun navigateToTargetFragment() {
        // Ensure the NavController is properly initialized
        if (::navController.isInitialized) {
            navController.navigate(R.id.TargetFragment)
        } else {
            Log.e("MainActivity", "NavController is not initialized")
        }
    }
    private fun navigateToRefFragment() {
        // Ensure the NavController is properly initialized
        if (::navController.isInitialized) {
            navController.navigate(R.id.ReferenceFragment)
        } else {
            Log.e("MainActivity", "NavController is not initialized")
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        navController = navHostFragment?.navController ?: throw IllegalStateException("NavController not found")

        Log.d("MainActivity", "NavController: $navController")
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
