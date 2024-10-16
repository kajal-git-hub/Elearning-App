package xyz.penpencil.competishun.ui.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.penpencil.competishun.AppController
import xyz.penpencil.competishun.databinding.FragmentBottomSheetDownloadBookmarkBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import xyz.penpencil.competishun.download.DownloadWorker
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts

@AndroidEntryPoint
class BottomSheetDownloadBookmark : BottomSheetDialogFragment() {
    private var itemDetails: TopicContentModel? = null
    private lateinit var binding: FragmentBottomSheetDownloadBookmarkBinding
    private lateinit var viewModel: VideourlViewModel

    @Inject
    lateinit var appController: AppController

    var fileName: String = ""
    var videoUrl: String = ""
    var videoFile : File?=null

    fun setItemDetails(details: TopicContentModel) {
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
                    downloadVideo(requireContext(), details.url, details.topicName)
                } else {
                    downloadPdf(details)
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

    private fun downloadPdf(details: TopicContentModel) {
        Log.d("DownloadPdf", "Starting download for: ${details.url} with topic name: ${details.topicName}")

        val fileName = "${details.topicName}.${details.fileType.lowercase()}"
        val pdfFile = File(requireContext().filesDir, fileName)

        if (pdfFile.exists()) {
            Toast.makeText(requireContext(), "PDF already downloaded Choose Other", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        Toast.makeText(requireContext(), "Download started....", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            val pdfUrl = details.url
            val fileName = "${details.topicName}.${details.fileType.lowercase()}"

            withContext(Dispatchers.IO) {
                try {
                    Log.d("DownloadPdf", "File name set to: $fileName")

                    val client = OkHttpClient()
                    val request = Request.Builder().url(pdfUrl).build()

                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) throw IOException("Failed to download file: $response")

                    Log.d("DownloadPdf", "Response received, starting file download.")

                    val pdfFile = File(requireContext().filesDir, fileName)
                    Log.d("DownloadPdf", "Saving file to: ${pdfFile.absolutePath}")

                    val inputStream: InputStream = response.body?.byteStream() ?: return@withContext
                    val outputStream = FileOutputStream(pdfFile)

                    inputStream.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                            Log.d("DownloadPdf", "File downloaded successfully.")
                        }
                    }

                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            Log.d("DownloadPdf", "Download success, showing toast.")
                            Toast.makeText(requireContext(), "PDF Download completed: $fileName", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                    }

                    details.url = pdfFile.absolutePath
                    storeItemInPreferences(details)
                    Log.d("DownloadPdf", "File path saved to preferences: ${pdfFile.absolutePath}")

                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("DownloadPdf", "Download failed: ${e.message}")
                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            Toast.makeText(requireContext(), "PDF Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun downloadVideo(context: Context, videoUrl: String, name: String) {
        Log.d("DownloadVideo", "Starting download for: $videoUrl with name: $name")

        fileName = "$name.mp4"
        videoFile = File(context.filesDir, fileName)

        if (videoFile?.exists() == true) {
            Toast.makeText(context, "Video already downloaded choose Other", Toast.LENGTH_SHORT).show()
            dismiss()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
        }else {
            checkPermissionNotification(videoUrl, fileName, videoFile.toString())
        }
    }

    private fun checkPermissionNotification(videoUrl: String, fileName: String, videoFile: String){
        val inputData = Data.Builder()
            .putString("url", videoUrl)
            .putString("fileName", fileName)
            .putString("filePath", videoFile.toString())
            .build()
        val downloadWork = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(inputData)
            .build()
        WorkManager.getInstance(requireActivity()).enqueue(downloadWork)
    }

    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            if (permission){
                checkPermissionNotification(videoUrl, fileName, videoFile.toString())
            }else {
                Toast.makeText(requireContext(), "Allow permission to download file", Toast.LENGTH_SHORT).show()
            }
        }

}