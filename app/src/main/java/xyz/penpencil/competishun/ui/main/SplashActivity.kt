package xyz.penpencil.competishun.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.ActivitySplashBinding

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

        userViewModel.fetchUserDetails()
        observeUserDetails()
    }
    private fun observeUserDetails() {
        userViewModel.userDetails.observe(this) { result ->
            result.onSuccess { data ->
                Log.d("userDetails", data.getMyDetails.fullName.toString())
                Log.d("userDetails", data.getMyDetails.userInformation.address?.city.toString())

                newUserOrNot = !data.getMyDetails.fullName.isNullOrEmpty()
                proceedBasedOnUserType()
            }.onFailure { exception ->
//                Toast.makeText(this, "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
                newUserOrNot = false
                proceedBasedOnUserType()
            }
        }
    }
    private fun proceedBasedOnUserType() {
        val token = sharedPreferencesManager.accessToken
        Log.e("token ", token.toString())
        Log.d("userdata", checkUserData().toString())

        if (newUserOrNot) {
            if (!token.isNullOrEmpty() && checkUserData()) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                setContentView(R.layout.welcome_screen)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!token.isNullOrEmpty() && checkUserData()) {
                        startActivity(Intent(this, HomeActivity::class.java))
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    finish()
                }, 2000)
            }, 2000)
        }
    }


    private fun checkUserData(): Boolean {
        return if (!sharedPreferencesManager.name.isNullOrEmpty() || !sharedPreferencesManager.city.isNullOrEmpty() || !sharedPreferencesManager.reference.isNullOrEmpty() || !sharedPreferencesManager.preparingFor.isNullOrEmpty() || sharedPreferencesManager.targetYear != 0) {
            false
        } else{
            true
        }
    }



}