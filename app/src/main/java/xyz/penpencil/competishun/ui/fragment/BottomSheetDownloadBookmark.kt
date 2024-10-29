package xyz.penpencil.competishun.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.AppController
import xyz.penpencil.competishun.databinding.FragmentBottomSheetDownloadBookmarkBinding
import java.io.File
import javax.inject.Inject
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.offline.TopicContentViewModel
import xyz.penpencil.competishun.utils.permissionList

@AndroidEntryPoint
class BottomSheetDownloadBookmark : BottomSheetDialogFragment() {
    private var itemDetails: TopicContentModel? = null
    private lateinit var binding: FragmentBottomSheetDownloadBookmarkBinding
    private lateinit var viewModel: VideourlViewModel

    @Inject
    lateinit var appController: AppController

    private var fileName: String = ""
    private var videoUrl: String = ""
    private var downloadPath: String = ""
    private var isExternal: Boolean = false

    private val topicContentViewModel: TopicContentViewModel by viewModels()


    fun setItemDetails(details: TopicContentModel) {
        Log.e("Details", "isExternal: ${details.isExternal}")
        itemDetails = details
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetDownloadBookmarkBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[VideourlViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvBookmark.setOnClickListener { bookmarkItem() }
        binding.tvDownload.setOnClickListener { downloadItem() }
    }

    private fun bookmarkItem() {
        itemDetails?.let {
            Log.d("Bookmark", "Clicked")
            SharedPreferencesManager(requireActivity()).saveDownloadedItemBm(it)
            Toast.makeText(requireContext(), "Added to Bookmarks", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun downloadItem() {
        itemDetails?.let { details ->
            val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
            val downloadLimitExceeded = checkDownloadLimits(details, sharedPreferencesManager)

            if (downloadLimitExceeded) return

            SharedPreferencesManager(requireActivity()).saveDownloadedItem(details)
            setupDownloadPaths(details)

            when (details.fileType) {
                "VIDEO" -> downloadVideo(details.url)
                "PDF" -> downloadPdf(details.url)
            }
            dismiss()
        }
    }

    private fun checkDownloadLimits(details: TopicContentModel, sharedPreferencesManager: SharedPreferencesManager): Boolean {
        val downloadedVideos = sharedPreferencesManager.getDownloadedVideos()
        val downloadedPdfs = sharedPreferencesManager.getDownloadedPdfs()

        return when {
            details.fileType == "VIDEO" && downloadedVideos.size >= 8 -> {
                showToast("You can only download up to 8 videos. Please delete some.")
                true
            }
            details.fileType == "PDF" && downloadedPdfs.size >= 8 -> {
                showToast("You can only download up to 8 PDFs. Please delete some.")
                true
            }
            else -> false
        }
    }

    private fun setupDownloadPaths(details: TopicContentModel) {
        isExternal = details.isExternal
        downloadPath = if (isExternal) "/storage/emulated/0/Download" else requireContext().filesDir.absolutePath
        val extension = if (details.fileType == "PDF")  ".pdf" else ".mp4"
        fileName = "${details.topicName}$extension"
    }

    private fun downloadPdf(url: String) {
        if (checkIfFileExists()) return
        videoUrl = url
        checkNotificationPermission()
    }

    private fun downloadVideo(url: String) {
        videoUrl = url
        if (checkIfFileExists()) return
        checkNotificationPermission()
    }

    private fun checkIfFileExists(): Boolean {
        val fileExists = File(downloadPath, fileName).isFile
        if (fileExists) showToast("File already downloaded, choose another")
        return fileExists
    }

    private fun checkNotificationPermission() {
        if (arePermissionsGranted()) {
            (requireActivity() as HomeActivity).downloadFile(videoUrl, fileName, isExternal)
            lifecycleScope.launch {
                itemDetails?.let { item ->
                    item.also { data ->
                        data.isExternal = isExternal
                        data.localPath = downloadPath
                    }
                    topicContentViewModel.insertTopicContent(item)
                }
            }
        } else {
            showPermissionDeniedDialog()
        }
    }

    private fun arePermissionsGranted(): Boolean {
        val missingPermissions = permissionList().filterNot {
            ActivityCompat.checkSelfPermission(requireActivity(), it) == PackageManager.PERMISSION_GRANTED
        }
        return if (missingPermissions.isEmpty()) {
            true
        } else {
            requestNotificationPermission.launch(missingPermissions.toTypedArray())
            false
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Notification Permission Needed")
            .setMessage("To show download progress, please allow notification permission.")
            .setPositiveButton("Settings") { dialog, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${requireActivity().packageName}")
                })
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions[Manifest.permission.POST_NOTIFICATIONS] == true
            } else {
                true
            }

            val allPermissionsGranted = permissionList().all { permissions[it] == true }
            if (notificationPermissionGranted && allPermissionsGranted) {
                (requireActivity() as HomeActivity).downloadFile(videoUrl, fileName, isExternal)
            } else {
                showPermissionDeniedDialog()
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
