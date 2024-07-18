package com.student.competishun.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
    val countryCode = "+91"
    val mobileNo = "7667022303"
    private val mainVM: MainVM by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.splash_screen)


        Handler(Looper.getMainLooper()).postDelayed({
            setContentView(R.layout.welcome_screen)

            Handler(Looper.getMainLooper()).postDelayed({
                setContentView(binding.root)

                enableEdgeToEdge()

                ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                    insets
                }

                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navController = navHostFragment.navController


                lifecycleScope.launch {
                    supervisorScope {
                        launch {
                            // Optional
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
                        viewModel.getOtp(countryCode, mobileNo)
                        viewModel.otpResult.observe(this@MainActivity, Observer { result ->
                            if (result == true) {
                                Log.e("otpGot", result.toString())
                            } else {
                                Log.e("otp not running", result.toString())
                            }
                        })
                    }


                }


            }, 2000)

        },2000)

    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
