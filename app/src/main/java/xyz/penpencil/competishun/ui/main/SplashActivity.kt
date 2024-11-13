package xyz.penpencil.competishun.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private var isMyCourseAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.navigationBarColor = ContextCompat.getColor(this,android.R.color.black)
        sharedPreferencesManager = SharedPreferencesManager(this)

        userViewModel.fetchUserDetails()
        observeUserDetails()
    }
    private fun observeUserDetails() {
        userViewModel.userDetails.observe(this) { result ->
            result.onSuccess { data ->
                Log.d("userfullName", data.getMyDetails.fullName.toString())
                Log.d("userDetails", data.getMyDetails.userInformation.address?.city.toString())

                isMyCourseAvailable  = data.getMyDetails.courses.isNotEmpty()
                Log.d("userDetails", "isMyCourseAvailable: $isMyCourseAvailable")
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
        Log.d("checkUserData", checkUserData().toString())
        Log.d("checknewUserOrNot", newUserOrNot.toString())
        Log.d("isMyCourseAvailable", isMyCourseAvailable.toString())


        if (newUserOrNot) {
            if (!token.isNullOrEmpty() && checkUserData()) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else if (!token.isNullOrEmpty() && isMyCourseAvailable) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("isMyCourseAvailable", true)
                startActivity(intent)
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                setContentView(R.layout.welcome_screen)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!token.isNullOrEmpty() && checkUserData() && isMyCourseAvailable) {
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
        Log.e("Datashared",sharedPreferencesManager.email.toString() + sharedPreferencesManager.name)
        Log.e("Datashre1",sharedPreferencesManager.name.toString() + sharedPreferencesManager.state)
        Log.e("Datashre1",sharedPreferencesManager.name.toString() + sharedPreferencesManager.state)
        return if (!sharedPreferencesManager.name.isNullOrEmpty() ||  !sharedPreferencesManager.state.isNullOrEmpty() || !sharedPreferencesManager.city.isNullOrEmpty() || !sharedPreferencesManager.email.isNullOrEmpty() || !sharedPreferencesManager.reference.isNullOrEmpty() || !sharedPreferencesManager.preparingFor.isNullOrEmpty() || sharedPreferencesManager.targetYear != 0) {
            false
        } else{
            true
        }
    }


    override fun onResume() {
        super.onResume()
        window.statusBarColor = ContextCompat.getColor(this, R.color.splash_blue)
    }

    override fun onStop() {
        super.onStop()
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
    }

}
