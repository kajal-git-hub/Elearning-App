package com.student.competishun.ui.main

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.student.competishun.R
import com.student.competishun.data.model.UpdateUserInput
import com.student.competishun.databinding.ActivityMainBinding
import com.student.competishun.ui.viewmodel.MainVM
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import com.student.competishun.ui.viewmodel.UserViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainVM: MainVM by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    lateinit var userInput: UpdateUserInput
    var userId :String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding = ActivityMainBinding.inflate(layoutInflater)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        navController = navHostFragment?.navController ?: throw IllegalStateException("NavController not found")

        sharedPreferencesManager = SharedPreferencesManager(this)
        userInput = UpdateUserInput()

        getUserInfo()

        val shouldNavigateToLogin = intent.getBooleanExtra("navigateToLogin", false)
        if (shouldNavigateToLogin) {
            navigateToLoginFragment()
        } else {
            setupInitialFlow()
        }
        setContentView(binding.root)
    }

    private fun navigateToLoginFragment() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(R.id.loginFragment)
        navController.graph = navGraph
    }

    private fun setupInitialFlow() {
        userId= sharedPreferencesManager.userId.toString()
        Log.d("aman1",sharedPreferencesManager.userId.toString())
        Log.d("aman2",sharedPreferencesManager.accessToken.toString())
        if (!sharedPreferencesManager.userId.isNullOrEmpty() && sharedPreferencesManager.accessToken.isNullOrEmpty()) {
            directLoginFlow()
        } else {
            onWelcomeFlow()
        }
    }

    private fun directLoginFlow() {
        Log.d("insidedirectlogin","true1")
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        val startDestination = R.id.loginFragment

        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
        setContentView(binding.root)
        navController.navigate(startDestination)
    }

    private fun onWelcomeFlow() {
        Log.d("insidedirectlogin","false1")
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        val startDestination = R.id.onWelcomeFragment

        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
        setContentView(binding.root)
        navController.navigate(startDestination)
        getUserInfo()
    }

    override fun onResume() {
        super.onResume()
        userViewModel.fetchUserDetails()
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

    private fun navigateToHomeActivity(userId:String) {
        // Replace with your actual userId
        Log.e("mainUserID",userId)
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("userId", userId)
        }
        startActivity(intent)
    }

    private fun getUserInfo() {
        when {
            sharedPreferencesManager.isReferenceSelectionInProgress -> navigateToRefFragment()
            !sharedPreferencesManager.reference.isNullOrEmpty() -> navigateToHomeActivity(userId)
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

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

