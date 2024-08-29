package com.student.competishun.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.ObservableField
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.student.competishun.R
import com.student.competishun.databinding.ActivityHomeBinding
import com.student.competishun.databinding.ActivityMainBinding
import com.student.competishun.databinding.ActivitySplashBinding
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferencesManager = SharedPreferencesManager(this)
        Handler(Looper.getMainLooper()).postDelayed({
            setContentView(R.layout.welcome_screen)
            Handler(Looper.getMainLooper()).postDelayed({
                val token = sharedPreferencesManager.refreshToken
                Log.e("token ", token.toString())
                if (!token.isNullOrEmpty() && isUserDataComplete() ) {
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }, 2000)
        }, 2000)

    }

    private fun isUserDataComplete(): Boolean {
        Log.e("mainActivity",sharedPreferencesManager.mobileNo.toString())
        Log.e("mainActivity",sharedPreferencesManager.name.toString())
        Log.e("mainActivity",sharedPreferencesManager.userId.toString())
        Log.e("mainActivity",sharedPreferencesManager.city.toString())
        Log.e("mainActivity",sharedPreferencesManager.reference.toString())
        Log.e("mainActivity",sharedPreferencesManager.preparingFor.toString())
        Log.e("mainActivity",sharedPreferencesManager.targetYear.toString())

        return sharedPreferencesManager.mobileNo?.isNotEmpty() == true &&
                sharedPreferencesManager.name?.isNotEmpty() == true &&
                sharedPreferencesManager.userId?.isNotEmpty() == true &&
                sharedPreferencesManager.city?.isNotBlank() == true &&
                sharedPreferencesManager.reference?.isNotEmpty() == true &&
                sharedPreferencesManager.preparingFor?.isNotEmpty() == true &&
                sharedPreferencesManager.targetYear != 0
    }

}