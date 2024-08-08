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
import com.student.competishun.databinding.ActivityMainBinding
import com.student.competishun.ui.viewmodel.GetOtpViewModel
import com.student.competishun.ui.viewmodel.MainVM
import com.student.competishun.ui.viewmodel.VerifyOtpViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainVM: MainVM by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Set the initial content view to the splash screen
        setContentView(R.layout.splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            // Switch to the welcome screen after a delay
            setContentView(R.layout.welcome_screen)

            Handler(Looper.getMainLooper()).postDelayed({


                // Switch to the main layout after a delay
                binding = ActivityMainBinding.inflate(layoutInflater)
                setContentView(binding.root)

                // Initialize SharedPreferencesManager after setting the content view
                sharedPreferencesManager = SharedPreferencesManager(this)

                // Check if the access token is available
                checkToken()

                // Setup edge-to-edge display
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

                // Initialize navigation components
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navController = navHostFragment.navController

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

            }, 2000)
        }, 2000)
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
