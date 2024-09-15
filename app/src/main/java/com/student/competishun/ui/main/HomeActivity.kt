package com.student.competishun.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.ObservableField
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.razorpay.PaymentResultListener
import com.student.competishun.R
import com.student.competishun.databinding.ActivityHomeBinding
import com.student.competishun.ui.fragment.AdditionalDetailsFragment
import com.student.competishun.ui.fragment.AddressDetailsFragment
import com.student.competishun.ui.fragment.AllDemoResourcesFree
import com.student.competishun.ui.fragment.AllFaqFragment
import com.student.competishun.ui.fragment.BookMarkFragment
import com.student.competishun.ui.fragment.BottomSheetDescriptionFragment
import com.student.competishun.ui.fragment.BottomSheetPersonalDetailsFragment
import com.student.competishun.ui.fragment.BottomSheetTSizeFragment
import com.student.competishun.ui.fragment.CourseEmptyFragment
import com.student.competishun.ui.fragment.CourseFragment
import com.student.competishun.ui.fragment.CoursesFragment
import com.student.competishun.ui.fragment.DownloadFragment
import com.student.competishun.ui.fragment.ExploreFragment
import com.student.competishun.ui.fragment.InstallmentDetailsBottomSheet
import com.student.competishun.ui.fragment.MediaPlayerFragment
import com.student.competishun.ui.fragment.MyCartFragment
import com.student.competishun.ui.fragment.MyPurchaseFragment
import com.student.competishun.ui.fragment.NEETFragment
import com.student.competishun.ui.fragment.NotificationFragment
import com.student.competishun.ui.fragment.PaymentFailedFragment
import com.student.competishun.ui.fragment.PaymentFragment
import com.student.competishun.ui.fragment.PaymentLoaderFragment
import com.student.competishun.ui.fragment.PersonalDetailsFragment
import com.student.competishun.ui.fragment.ProfileFragment
import com.student.competishun.ui.fragment.RecommendViewDetail
import com.student.competishun.ui.fragment.ResumeCourseFragment
import com.student.competishun.ui.fragment.ScheduleFragment
import com.student.competishun.ui.fragment.SubjectContentFragment
import com.student.competishun.ui.fragment.TopicTypeContentFragment
import com.student.competishun.ui.viewmodel.UserViewModel
import com.student.competishun.ui.viewmodel.VerifyOtpViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityHomeBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var callIcon: ImageView
    private var isCallingSupportVisible = ObservableField(true)
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    var courseType:String = ""
     var userId:String = ""
    val bundle = Bundle()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drwaer_layout)
        navigationView = findViewById(R.id.nv_navigationView)

        sharedPreferencesManager = SharedPreferencesManager(this)





        binding.clStartCall.setOnClickListener {
            val phoneNumber = "8888000021"
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }

        bottomNavigationView = findViewById(R.id.bottomNav)
        callIcon = findViewById(R.id.ig_ContactImage)


        val savePaymentSuccess = sharedPreferencesManager.getBoolean("savePaymentSuccess", false)
        val bottomNavigationView = binding.bottomNav
        val menu = bottomNavigationView.menu
        sharedPreferencesManager = SharedPreferencesManager(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentNavigation) as NavHostFragment
        navController = navHostFragment.navController

        Log.e("courseTu[ea",courseType)

        if (savePaymentSuccess) {
            menu.findItem(R.id.News).isVisible = false
            menu.findItem(R.id.Chat).isVisible = true
        } else {
            menu.findItem(R.id.News).isVisible = true
            menu.findItem(R.id.Chat).isVisible = false
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    if (navController.currentDestination?.id != R.id.homeFragment) {
                        navController.navigate(R.id.homeFragment)
                    }
                    true
                }
                R.id.myCourse -> {
                    if (navController.currentDestination?.id != R.id.PersonalDetailsFragment) {
                        navController.navigate(R.id.courseEmptyFragment)
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState == null) {

            // Ensure that HomeFragment is loaded on the first launch
            navController.navigate(R.id.homeFragment,bundle)
            bottomNavigationView.selectedItemId = R.id.home

        }

    }
    override fun onBackPressed() {

        if (navController.currentDestination?.id != R.id.homeFragment) {
            Log.e("cousswetype",bundle.toString())
            navController.navigate(R.id.homeFragment,bundle)
            binding.bottomNav.selectedItemId = R.id.home
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        userId = sharedPreferencesManager.userId.toString()
        Log.e("Sharedhome $userId", intent.getStringExtra("userId").toString())
    }


    fun showBottomNavigationView(show:Boolean){
        bottomNavigationView.visibility = if(show) View.VISIBLE else View.GONE
    }
    fun showFloatingButton(show:Boolean){
        callIcon.visibility =  if(show) View.VISIBLE else View.GONE
    }


    override fun onPaymentSuccess(p0: String?) {
        navigateToLoaderScreen()
        Log.e("success kajal", "success : $p0")
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        navigatePaymentFail()
        Log.e("success kajal", "fail $p1 and $p0")
    }

    private fun navigatePaymentFail() {
        navController.navigate(R.id.PaymentFailedFragment)
    }

    private fun navigateToLoaderScreen() {
    Log.e("homeuserIDload",userId.toString())
        navController.navigate(R.id.paymentLoaderFragment)
        val bundle = Bundle().apply {
            putString("userId", userId)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (navController.currentDestination?.id == R.id.paymentLoaderFragment) {
                navController.navigate(R.id.action_paymentLoaderFragment_to_paymentFragment,bundle)
            }
        }, 2000)
    }
}
