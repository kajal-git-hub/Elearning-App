package com.student.competishun.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.student.competishun.R
import com.student.competishun.databinding.ActivityHomeBinding
import com.student.competishun.ui.fragment.CallingSupport
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityHomeBinding
    private var isCallingSupportFragmentVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.igContactImage.setOnClickListener {
            if (isCallingSupportFragmentVisible) {
                closeCallingSupportFragment()
            } else {
                openCallingSupportFragment()
            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentNavigation) as NavHostFragment
        navController = navHostFragment.navController

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun openCallingSupportFragment() {
        val callingSupportFragment = CallingSupport()
        callingSupportFragment.show(supportFragmentManager, "CallingSupportFragment")
        isCallingSupportFragmentVisible = true
    }

    private fun closeCallingSupportFragment() {
        val fragment = supportFragmentManager.findFragmentByTag("CallingSupportFragment") as? DialogFragment
        fragment?.dismiss()
        isCallingSupportFragmentVisible = false
    }
}
