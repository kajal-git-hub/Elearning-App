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
import com.student.competishun.ui.viewmodel.UserViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainVM: MainVM by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    lateinit var userInput: UpdateUserInput

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Initialize SharedPreferencesManager
        sharedPreferencesManager = SharedPreferencesManager(this)
        userInput = UpdateUserInput()

        // Observe the user details LiveData
        userViewModel.userDetails.observe(this) { result ->
            result.onSuccess { data ->
                val userDetails = data.getMyDetails
                if (isUserDataComplete()) {
                    // User data is complete, navigate to HomeActivity
                    navigateToHomeActivity()
                } else {
                    // Store necessary data in SharedPreferencesManager
                    sharedPreferencesManager.mobileNo = userDetails.mobileNumber
                }
            }.onFailure { exception ->
                Log.e("mainActivitydetails", exception.message.toString())
                //  Toast.makeText(this, "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Fetch user details
        userViewModel.fetchUserDetails()

        // Show splash and welcome screens if user data is not complete
        if (!isUserDataComplete()) {
            showSplashAndWelcomeScreens()
        }
    }

    override fun onResume() {
        super.onResume()
        userViewModel.fetchUserDetails()
    }

    private fun isUserDataComplete(): Boolean {
        return sharedPreferencesManager.mobileNo?.isNotEmpty() == true &&
                sharedPreferencesManager.name?.isNotEmpty() == true &&
                sharedPreferencesManager.userId?.isNotEmpty() == true &&
                sharedPreferencesManager.city?.isNotBlank() == true &&
                sharedPreferencesManager.reference?.isNotEmpty() == true &&
                sharedPreferencesManager.preparingFor?.isNotEmpty() == true &&
                sharedPreferencesManager.targetYear != 0
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Close the MainActivity
    }

    private fun showSplashAndWelcomeScreens() {
        setContentView(R.layout.splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            // Switch to the welcome scrmyeen after a delay
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
                val token = sharedPreferencesManager.accessToken
                Log.e("token ", token.toString())
                if (token.isNullOrEmpty()) {
                    navController.navigate(R.id.onWelcomeFragment)
                } else
                    getUserInfo()
            }, 2000)
        }, 2000)
    }

    private fun getUserInfo() {
        when {
            !sharedPreferencesManager.reference.isNullOrEmpty() -> navigateToHomeActivity()
            !sharedPreferencesManager.preparingFor.isNullOrEmpty() -> navigateToTargetFragment()
            sharedPreferencesManager.targetYear != 0 -> navigateToRefFragment()
            !sharedPreferencesManager.name.isNullOrEmpty() && !sharedPreferencesManager.city.isNullOrEmpty() -> navigateToPreparationFragment()
            else -> Unit
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

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        navController =
            navHostFragment?.navController ?: throw IllegalStateException("NavController not found")
        Log.d("MainActivity", "NavController: $navController")
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

