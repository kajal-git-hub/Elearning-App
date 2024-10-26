package xyz.penpencil.competishun.ui.main

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.ObservableField
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.eventbus.Subscribe
import com.ketch.Ketch
import com.ketch.Status
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.ActivityHomeBinding
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.EventBusSingleton
import xyz.penpencil.competishun.utils.MyEvent
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityHomeBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var callIcon: ImageView
    private var isCallingSupportVisible = ObservableField(true)
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    var courseType: String = ""
    var userId: String = ""
    private var DetailAvailable = false
    val bundle = Bundle()
    private var navigateToFragment = false

    private var previousSelectedItemId: Int = R.id.home

    @Inject
    lateinit var ketch: Ketch

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        sharedPreferencesManager = SharedPreferencesManager(this)
        onBackPressedDispatcher.addCallback(this, backPressListener)
        window.navigationBarColor = ContextCompat.getColor(this,android.R.color.white)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentNavigation) as NavHostFragment
        navController = navHostFragment.navController

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userViewModel.fetchUserDetails()
        observeUserDetails()

        bottomNavigationView = findViewById(R.id.bottomNav)
        callIcon = findViewById(R.id.ig_ContactImage)

        navigateToFragment = intent.getBooleanExtra("isMyCourseAvailable", false)
        val navigateFromVerify = intent.getStringExtra("navigateTo")
        if (navigateToFragment) {
            navController.navigate(R.id.courseEmptyFragment)
            binding.bottomNav.selectedItemId = R.id.myCourse // Set the selected tab to My Course
        } else if (navigateFromVerify == "CourseEmptyFragment") {
            navController.navigate(R.id.courseEmptyFragment)
            binding.bottomNav.selectedItemId = R.id.myCourse
        } else {
            if (savedInstanceState == null) {
                navController.navigate(R.id.homeFragment, bundle)
                binding.bottomNav.selectedItemId = R.id.home
            }
        }

        binding.clStartCall.setOnClickListener {
            val phoneNumber = "8888000021"
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
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


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.bottomNav.selectedItemId = R.id.home
                }
                R.id.courseEmptyFragment ->{
//                    binding.bottomNav.selectedItemId = R.id.myCourse
                }
                R.id.bookmark -> {
                    binding.bottomNav.selectedItemId = R.id.bookmark
                }

                R.id.download -> {
                    binding.bottomNav.selectedItemId = R.id.download
                }


                else -> {
//                    supportActionBar?.show()
                }
            }
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

                R.id.Download -> {
                    navController.navigate(R.id.DownloadFragment)
                    true
                }

                R.id.Bookmark -> {
                    navController.navigate(R.id.BookMarkFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }


    }
    private fun navigateToHomeFragment(navController: NavController, arguments: Bundle?) {
//        navController.navigateTo(R.id.navigation_home, arguments)
    }

    private val backPressListener = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when (navController.currentDestination?.id) {

                R.id.BookMarkFragment -> {
                    navController.popBackStack(R.id.homeFragment, false)
                    binding.bottomNav.selectedItemId = R.id.home
                }

                R.id.DownloadFragment -> {
                    navController.popBackStack(R.id.homeFragment, false)
                    binding.bottomNav.selectedItemId = R.id.home
                }

                R.id.courseEmptyFragment -> {
                    navController.popBackStack(R.id.homeFragment, false)
                    binding.bottomNav.selectedItemId = R.id.home
                }

                R.id.ResumeCourseFragment -> {
                    navController.navigate(R.id.courseEmptyFragment)
                    binding.bottomNav.selectedItemId = R.id.myCourse
                }

                R.id.SubjectContentFragment -> {
                    navController.navigate(R.id.ResumeCourseFragment)
                    binding.bottomNav.selectedItemId = R.id.myCourse
                }

                else -> {
                    if (navController.currentDestination?.id == R.id.home) {
                        finish()
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }
    }

    fun showBottomNavigationView(show: Boolean) {
        bottomNavigationView.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun showFloatingButton(show: Boolean) {
        callIcon.visibility = if (show) View.VISIBLE else View.GONE
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
        Log.e("homeuserIDload", userId.toString())
        // navController.navigate(R.id.paymentLoaderFragment)
        val bundle = Bundle().apply {
            putString("userId", userId)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            navController.navigate(R.id.paymentFragment, bundle)
        }, 2000)
    }

    override fun onResume() {
        super.onResume()
        userId = sharedPreferencesManager.userId.toString()

    }

    private fun observeUserDetails() {
        userViewModel.userDetails.observe(this) { result ->
            result.onSuccess { data ->

                DetailAvailable = data.getMyDetails.courses.isNotEmpty()
                val bottomNavigationView = binding.bottomNav
                val menu = bottomNavigationView.menu
                if (DetailAvailable) {
                    Log.d("DetailAvailable", DetailAvailable.toString())
                    menu.findItem(R.id.Bookmark).isVisible = true
                    menu.findItem(R.id.Store).isVisible = false
                } else {
                    menu.findItem(R.id.Bookmark).isVisible = false
                    menu.findItem(R.id.Store).isVisible = true
                }
            }.onFailure { exception ->
                Toast.makeText(
                    this,
                    "Error fetching details: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    fun downloadFile(url: String, fileName: String, path: String = filesDir.absolutePath){
        val id = ketch.download(
            url = url,
            fileName = fileName,
            path = path
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                ketch.observeDownloadById(id)
                    .flowOn(Dispatchers.IO)
                    .collect { downloadModel ->
                        when (downloadModel.status) {
                            Status.STARTED -> {
                                showStatus("Download has started.")
                            }
                            Status.SUCCESS -> {
                                showStatus("Download completed successfully.")
                            }
                            Status.CANCELLED -> {
                                showStatus("Download was cancelled.")
                            }
                            Status.FAILED -> {
                                showStatus("Download failed.")
                            }

                            Status.QUEUED -> {}
                            Status.PROGRESS -> {}
                            Status.PAUSED -> {}
                            Status.DEFAULT -> {}
                        }
                    }
            }
        }
    }

    private fun showStatus(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
