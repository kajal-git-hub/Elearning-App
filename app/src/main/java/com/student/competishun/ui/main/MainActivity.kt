package com.student.competishun.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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






}
