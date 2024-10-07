package xyz.penpencil.competishun.ui.main

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import xyz.penpencil.competishun.data.model.UpdateUserInput
import xyz.penpencil.competishun.ui.viewmodel.MainVM
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.ActivityMainBinding

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
        Log.d("insidedirectlogin","directlogin")
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        val startDestination = R.id.loginFragment

        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
        setContentView(binding.root)
        navController.navigate(startDestination)
    }

    private fun onWelcomeFlow() {
        Log.d("insideonwelcome","onwelcome")
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

    private fun navigateToHomeActivity(userId:String) {
        // Replace with your actual userId
        Log.e("mainUserID",userId)
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("userId", userId)
        }
        startActivity(intent)
        finish()
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

