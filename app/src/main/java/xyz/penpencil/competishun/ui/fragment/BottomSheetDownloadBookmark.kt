package xyz.penpencil.competishun.ui.fragment

import android.content.Context
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
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.utils.permissionList

@AndroidEntryPoint
class BottomSheetDownloadBookmark : BottomSheetDialogFragment() {
    private var itemDetails: TopicContentModel? = null
    private lateinit var binding: FragmentBottomSheetDownloadBookmarkBinding
    private lateinit var viewModel: VideourlViewModel

    @Inject
    lateinit var appController: AppController

    var fileName: String = ""
    private var videoUrl: String = ""
    private var videoFile : File?=null
    private var isExternal : Boolean = false

    fun setItemDetails(details: TopicContentModel) {
        Log.e("jcxjchzxcjzx", "setItemDetails: " +details.isExternal )
        this.itemDetails = details
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetDownloadBookmarkBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(VideourlViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvBookmark.setOnClickListener {
            itemDetails?.let { details ->
                Log.d("bookmark","Clicked")
                storeItemInPreferencesBm(details)
                Toast.makeText(requireContext(),"Added in BookMark",Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        binding.tvDownload.setOnClickListener {
            itemDetails?.let { details ->
                Log.d("download","Clicked")
                Log.d("ItemDetails", details.toString())

                val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
                val downloadedVideos = sharedPreferencesManager.getDownloadedVideos()
                val downloadedPdfs = sharedPreferencesManager.getDownloadedPdfs()

                // Check download limits
                if (details.fileType == "VIDEO" && downloadedVideos.size >= 8) {
                    Toast.makeText(requireContext(), "You can only download up to 8 videos. Please delete some.", Toast.LENGTH_SHORT).show()
                    return@let
                } else if (details.fileType == "PDF" && downloadedPdfs.size >= 8) {
                    Toast.makeText(requireContext(), "You can only download up to 8 PDFs. Please delete some.", Toast.LENGTH_SHORT).show()
                    return@let
                }

                storeItemInPreferences(details)

                if (details.fileType == "VIDEO") {
                    videoUrl =  details.url
                    downloadVideo(requireContext(), details.url, details)
                } else {
                    downloadPdf(details, requireContext())
                }
                dismiss()
            }
        }

    }
    private fun storeItemInPreferencesBm(item: TopicContentModel) {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        sharedPreferencesManager.saveDownloadedItemBm(item)
    }

    private fun storeItemInPreferences(item: TopicContentModel) {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        sharedPreferencesManager.saveDownloadedItem(item)
    }

    private fun downloadPdf(details: TopicContentModel, requireContext: Context) {
        this.videoUrl = videoUrl
        this.fileName = "${details.topicName}.${details.fileType.lowercase()}"
        this.videoFile = if (itemDetails?.isExternal == true) File("/storage/emulated/0/Documents") else requireContext.filesDir
       if (videoFile?.isDirectory != true){
           videoFile?.mkdirs()
       }
        isExternal = itemDetails?.isExternal == true

        if (File(videoFile?.absolutePath, fileName).exists()) {
            Toast.makeText(context, "Video already downloaded, choose other", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }
        checkNotificationPermission()
    }

    private fun downloadVideo(context: Context, videoUrl: String, topicContentModel: TopicContentModel) {
        this.videoUrl = videoUrl
        this.fileName = "${topicContentModel.topicName}.mp4"
        this.videoFile = if (itemDetails?.isExternal == true) File("/storage/emulated/0/Movies") else context.filesDir
        if (videoFile?.isDirectory != true){
            videoFile?.mkdirs()
        }
        isExternal = itemDetails?.isExternal == true

        if (File(videoFile?.absolutePath, fileName).exists()) {
            Toast.makeText(context, "Video already downloaded, choose other", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }
        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        if (checkAndRequestPermissions()) {
            (requireActivity() as HomeActivity).downloadFile(
                url = videoUrl,
                fileName = fileName,
                path = videoFile?.absolutePath!!
            )
        }else {
            showPermissionDeniedDialog()
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val missingPermissions = permissionList().filter {
            ActivityCompat.checkSelfPermission(requireActivity(), it) != PackageManager.PERMISSION_GRANTED
        }
        return if (missingPermissions.isEmpty()) {
            true
        } else {
            requestNotificationPermission.launch(missingPermissions.toTypedArray())
            false
        }
    }

    private fun showPermissionDeniedDialog() {
        val activity = activity ?: return
        AlertDialog.Builder(requireActivity())
            .setTitle("Notification Permission Needed")
            .setMessage("To show download progress, please allow notification permission.")
            .setPositiveButton("Settings") { dialog, _ ->
                activity.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${activity.packageName}")
                })
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private val requestNotificationPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.POST_NOTIFICATIONS] == true
        } else {
            true
        }

        val allPermissionsGranted = permissionList().all { permissions[it] == true }

        if (notificationPermissionGranted && allPermissionsGranted) {
            (requireActivity() as HomeActivity).downloadFile(
                url = videoUrl,
                fileName = fileName,
                path = videoFile?.absolutePath!!
            )
        } else {
            showPermissionDeniedDialog()
        }
    }
}