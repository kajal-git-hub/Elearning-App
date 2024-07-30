package com.student.competishun.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.student.competishun.R
import com.student.competishun.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import com.student.competishun.ui.fragment.AllDemoResourcesFree

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityHomeBinding
    private var isCallingSupportVisible = ObservableField(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.igContactImage.setOnClickListener {
            if (isCallingSupportVisible.get() == true) {
                binding.clCallingSupport.visibility = View.VISIBLE
                binding.igContactImage.setImageResource(R.drawable.fab_icon)
                isCallingSupportVisible.set(false)
            } else {
                binding.clCallingSupport.visibility = View.GONE
                binding.igContactImage.setImageResource(R.drawable.ic_call)
                isCallingSupportVisible.set(true)
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentNavigation) as NavHostFragment
        navController = navHostFragment.navController

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                super.onFragmentResumed(fm, f)
                binding.igContactImage.visibility = if (shouldHideContactImage(f)) View.GONE else View.VISIBLE
            }
        }, true)
    }

    private fun shouldHideContactImage(fragment: Fragment): Boolean {
        return fragment is AllDemoResourcesFree
    }
}
