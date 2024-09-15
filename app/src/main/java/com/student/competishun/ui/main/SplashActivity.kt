package com.student.competishun.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.student.competishun.R
import com.student.competishun.databinding.ActivityHomeBinding
import com.student.competishun.databinding.ActivityMainBinding
import com.student.competishun.databinding.ActivitySplashBinding
import com.student.competishun.ui.viewmodel.UserViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    private var newUserOrNot = false
    private val userViewModel: UserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferencesManager = SharedPreferencesManager(this)

//        observeUserDetails()
//        userViewModel.fetchUserDetails()

//        if(newUserOrNot){
//
//        }


        Handler(Looper.getMainLooper()).postDelayed({
            setContentView(R.layout.welcome_screen)
            Handler(Looper.getMainLooper()).postDelayed({
                val token = sharedPreferencesManager.accessToken
                Log.e("token ", token.toString())
                Log.d("userdata",checkUserData().toString())
                if (!token.isNullOrEmpty() && checkUserData()) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                else
                {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }, 2000)
        }, 2000)

    }

    private fun checkUserData(): Boolean {
        return if (sharedPreferencesManager.name.isNullOrEmpty() || sharedPreferencesManager.city.isNullOrEmpty() || sharedPreferencesManager.reference.isNullOrEmpty() || sharedPreferencesManager.preparingFor.isNullOrEmpty() || sharedPreferencesManager.targetYear == 0) {
            false
        } else{
            true
        }
    }

    private fun observeUserDetails() {
        userViewModel.userDetails.observe(this) { result ->
            result.onSuccess { data ->
                Log.d("userDetails",data.getMyDetails.fullName.toString())
                Log.d("userDetails",data.getMyDetails.userInformation.address?.city.toString())
                if(data.getMyDetails.id!=null){
                    newUserOrNot = true //registered user condition hai ye
                }else{
                    newUserOrNot = false  // non registered user condition hai ye
                }


            }.onFailure { exception ->
                Toast.makeText(this, "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

}
