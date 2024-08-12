package com.student.competishun.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.razorpay.PaymentResultListener
import com.student.competishun.R
import com.student.competishun.databinding.ActivityHomeBinding
import com.student.competishun.ui.fragment.AdditionalDetailsFragment
import com.student.competishun.ui.fragment.AddressDetailsFragment
import com.student.competishun.ui.fragment.AllDemoResourcesFree
import com.student.competishun.ui.fragment.AllFaqFragment
import com.student.competishun.ui.fragment.BottomSheetDescriptionFragment
import com.student.competishun.ui.fragment.CourseEmptyFragment
import com.student.competishun.ui.fragment.ExploreFragment
import com.student.competishun.ui.fragment.MyCartFragment
import com.student.competishun.ui.fragment.PaymentFragment
import com.student.competishun.ui.fragment.PaymentLoaderFragment
import com.student.competishun.ui.fragment.PersonalDetailsFragment
import com.student.competishun.ui.fragment.ResumeCourseFragment
import com.student.competishun.ui.fragment.SubjectContentFragment
import com.student.competishun.ui.fragment.TopicTypeContentFragment
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityHomeBinding
    private var isCallingSupportVisible = ObservableField(true)
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bottomNavigationView = binding.bottomNav
        bottomNavigationView.selectedItemId = R.id.home
        sharedPreferencesManager = SharedPreferencesManager(this)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    if (::navController.isInitialized) {
                        navController.navigate(R.id.homeFragment)
                    }
                    true
                }

                R.id.myCourse -> {
                    if (::navController.isInitialized) {
                        navController.navigate(R.id.PersonalDetailsFragment)
                    }
                    true
                }

                else -> false
            }
        }

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

        supportFragmentManager.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                super.onFragmentResumed(fm, f)
                updateUiVisibility(f)
            }
        }, true)
    }

    override fun onResume() {
        super.onResume()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentNavigation)
        if (currentFragment != null) {
            updateUiVisibility(currentFragment)
        }
    }

    private fun updateUiVisibility(fragment: Fragment) {
        binding.igContactImage.visibility =
            if (shouldHideContactImage(fragment)) View.GONE else View.VISIBLE
        binding.bottomNav.visibility =
            if (shouldHideBottomNav(fragment)) View.GONE else View.VISIBLE
    }

    private fun shouldHideContactImage(fragment: Fragment): Boolean {
        val fragmentsToHide = listOf(
            AllDemoResourcesFree::class.java,
            MyCartFragment::class.java,
            AllFaqFragment::class.java,
            PaymentFragment::class.java,
            PaymentLoaderFragment::class.java,
            PersonalDetailsFragment::class.java,
            AddressDetailsFragment::class.java,
            AdditionalDetailsFragment::class.java,
            ResumeCourseFragment::class.java,
            SubjectContentFragment::class.java,
            CourseEmptyFragment::class.java,
            BottomSheetDescriptionFragment::class.java,
            TopicTypeContentFragment::class.java,
            ExploreFragment::class.java,
        )
        return fragmentsToHide.any { it.isInstance(fragment) }
    }

    private fun shouldHideBottomNav(fragment: Fragment): Boolean {
        val fragmentsToHide = listOf(
            AllDemoResourcesFree::class.java,
            MyCartFragment::class.java,
            AllFaqFragment::class.java,
            PaymentFragment::class.java,
            ExploreFragment::class.java,
            PaymentLoaderFragment::class.java,
            PersonalDetailsFragment::class.java,
            AddressDetailsFragment::class.java,
            AdditionalDetailsFragment::class.java,
            CourseEmptyFragment::class.java,
            SubjectContentFragment::class.java,
            ResumeCourseFragment::class.java,
            BottomSheetDescriptionFragment::class.java,
            TopicTypeContentFragment::class.java,
        )
        return fragmentsToHide.any { it.isInstance(fragment) }
    }

    override fun onPaymentSuccess(p0: String?) {
        navigateToLoaderScreen()
        Log.e("success kajal","success : $p0")
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        navigatePaymentFail()
        Log.e("success kajal","fail $p1 and $p0")
    }
    private fun navigatePaymentFail(){
       navController.navigate(R.id.PaymentFailedFragment)
    }

    private fun navigateToLoaderScreen() {
       navController.navigate(R.id.paymentLoaderFragment)

        Handler(Looper.getMainLooper()).postDelayed({
            if (navController.currentDestination?.id == R.id.paymentLoaderFragment) {
                navController.navigate(R.id.action_paymentLoaderFragment_to_paymentFragment)
            }
        }, 2000)
    }
}
