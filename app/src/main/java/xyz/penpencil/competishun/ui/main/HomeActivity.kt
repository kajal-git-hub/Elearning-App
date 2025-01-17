package xyz.penpencil.competishun.ui.main

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.ketch.DownloadModel
import com.ketch.Ketch
import com.ketch.Status
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.BuildConfig
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.ActivityHomeBinding
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import java.io.File
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
    private val VIDEO_MIME_TYPE = "video/mp4"
    private val PDF_MIME_TYPE = "application/pdf"
    private val RELATIVE_PATH = Environment.DIRECTORY_DOWNLOADS

    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
//        changeStatusBarColor()
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        sharedPreferencesManager = SharedPreferencesManager(this)
        appUpdateManager = if (BuildConfig.DEBUG) {
            //TODO: need to test fake update manager
            FakeAppUpdateManager(this).apply {
                setUpdateAvailable(208)
            }
        } else {
            AppUpdateManagerFactory.create(this)
        }

        onBackPressedDispatcher.addCallback(this, backPressListener)
        window.navigationBarColor = ContextCompat.getColor(this,android.R.color.black)

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

        navigateToFragment =  if (intent.getBooleanExtra("isMyCourseAvailable", false)) {
            true
        }else {
            sharedPreferencesManager.isMyCourseAvailable
        }
        Log.e("navigateToFragment", "onCreate: $navigateToFragment", )
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
                R.id.Store ->{
                    val storeUrl = "http://store.competishun.com/"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl))
                    startActivity(intent)
                    true
                }

                else -> {
                    false
                }
            }
        }

        // TODO: add condition if required to update app
        checkForUpdate()

    }

    private fun checkForUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            when {
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {
                    startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
                }
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                    startUpdate(appUpdateInfo, AppUpdateType.FLEXIBLE)
                }
            }
        }
    }

    private fun startUpdate(appUpdateInfo: AppUpdateInfo, updateType: Int) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            activityResultLauncher,
            AppUpdateOptions.newBuilder(updateType).build()
        )
    }

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                Log.v("MyActivity", "Update flow completed!")
            }
            Activity.RESULT_CANCELED -> {
                Log.v("MyActivity", "User cancelled Update flow!")
            }
            else -> {
                Log.v("MyActivity", "Update flow failed with resultCode:${result.resultCode}")
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
//        binding.bottomNav.selectedItemId = R.id.home
//        binding.bottomNav.selectedItemId = R.id.home
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate()
            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "An update has been downloaded. Restart to complete.",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Restart") {
            appUpdateManager.completeUpdate()
        }.show()
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

    fun hideCallingSupport() {
        binding.clCallingSupport.visibility = View.GONE
        binding.igContactImage.setImageResource(R.drawable.ic_call)
        isCallingSupportVisible.set(true)
    }



    private fun createFile(fileName: String): Uri? {
        val relativePath = Environment.DIRECTORY_DOWNLOADS

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
        } else {
            val dir = Environment.getExternalStoragePublicDirectory(relativePath)
            val file = File(dir, fileName).apply { if (exists()) delete() }

            return try {
                if (file.createNewFile()) Uri.fromFile(file) else {
                    Log.e("CreateFile", "Failed to create file: $fileName")
                    null
                }
            } catch (e: Exception) {
                Log.e("CreateFile", "IOException while creating file: ${e.message}")
                null
            }
        }
    }


    private fun Uri.toFilePath(context: Context): String? {
        return when (scheme) {
            ContentResolver.SCHEME_CONTENT -> {
                val projection = arrayOf(MediaStore.Video.Media.DATA)
                context.contentResolver.query(this, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                        cursor.getString(columnIndex)
                    } else null
                }
            }
            ContentResolver.SCHEME_FILE -> path
            else -> null
        }
    }

    private fun handleDownloadStatus(downloadModel: DownloadModel) {
        when (downloadModel.status) {
            Status.STARTED -> showStatus("Download has started.")
            Status.SUCCESS -> showStatus("Download completed successfully.")
            Status.CANCELLED -> showStatus("Download was cancelled.")
            Status.FAILED -> {
                Log.e("DownloadError", "Failed reason: ${downloadModel.failureReason}")
                showStatus("Download failed.")
            }
            Status.QUEUED, Status.PROGRESS, Status.PAUSED, Status.DEFAULT -> {}
        }
    }
    fun downloadFile(url: String, fileName: String, isExternal: Boolean = false) {// ture -> external  // false -->
        val path = if (isExternal) {
            "/storage/emulated/0/Download/"
        } else {
            filesDir.absolutePath
        }

        Log.e("DownloadError", "PATH: $path$fileName")
        val id = ketch.download(
            url = url,
            fileName = fileName,
            path = path)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                ketch.observeDownloadById(id)
                    .flowOn(Dispatchers.IO)
                    .collect { handleDownloadStatus(it) }
            }
        }
    }

    private fun showStatus(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
