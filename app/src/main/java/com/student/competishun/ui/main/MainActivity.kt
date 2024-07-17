package com.student.competishun.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.student.competishun.R
import com.student.competishun.databinding.ActivityMainBinding
import com.student.competishun.ui.viewmodel.GetOtpViewModel
import com.student.competishun.ui.viewmodel.MainVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: GetOtpViewModel by viewModels()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val countryCode = "+91"
    val mobileNo = "7667022303"
    private val mainVM: MainVM by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Install splash screen
        installSplashScreen()

        // Set window insets listener for handling system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find NavHostFragment and NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Hide the action bar title
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Observe loader state using lifecycleScope
        lifecycleScope.launch {
            supervisorScope {
                launch {
                    // Optional: Add any concurrent tasks if needed
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
//        lifecycleScope.launch {
//            supervisorScope {
//                launch {
//
//                }
//                launch {
//                    mainVM.loader.collect{
//                        if(it){
//
//                        }else{
//
//                        }
//                    }
//                }
//            }
//        }
        viewModel.getOtp(countryCode,mobileNo)
        viewModel.otpResult.observe(this, Observer { result ->
            if (result == true){
                Log.e("otpGot",result.toString())
            }else{
                Log.e("otp not running",result.toString())
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
