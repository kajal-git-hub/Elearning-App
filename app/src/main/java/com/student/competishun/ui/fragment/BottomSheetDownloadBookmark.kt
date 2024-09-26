package com.student.competishun.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.student.competishun.R
import com.student.competishun.data.model.TopicContentModel
import com.student.competishun.databinding.FragmentBottomSheetDownloadBookmarkBinding
import com.student.competishun.ui.viewmodel.VideourlViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class BottomSheetDownloadBookmark : BottomSheetDialogFragment() {
    private var itemDetails: TopicContentModel? = null
    private lateinit var binding: FragmentBottomSheetDownloadBookmarkBinding
    private lateinit var viewModel: VideourlViewModel

    fun setItemDetails(details: TopicContentModel) {
        this.itemDetails = details
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            // Bookmark functionality
        }

        binding.tvDownload.setOnClickListener {
            itemDetails?.let { details ->
                Log.d("ItemDetails", details.toString())
                storeItemInPreferences(details)
                if(details.fileType=="VIDEO")
                {
                     downloadVideo(requireContext(),details.url,details.topicName)
                }
                else{
                    downloadPdf(details)
                }
                dismiss()
            }
        }
    }

    private fun storeItemInPreferences(item: TopicContentModel) {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        sharedPreferencesManager.saveDownloadedItem(item)
    }

    private fun downloadPdf(details: TopicContentModel) {
        lifecycleScope.launch {
            val pdfUrl = details.url // Assuming details.url contains the PDF URL
            val fileName = "${details.topicName}.${details.fileType.lowercase()}"

            withContext(Dispatchers.IO) {
                try {
                    // Create OkHttpClient and request
                    val client = OkHttpClient()
                    val request = Request.Builder().url(pdfUrl).build()

                    // Execute the request
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    // Save the PDF to internal storage
                    val pdfFile = File(requireContext().filesDir, fileName)
                    Log.d("PdfFile",pdfFile.toString())
                    val inputStream: InputStream = response.body?.byteStream() ?: return@withContext
                    val outputStream = FileOutputStream(pdfFile)

                    inputStream.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }

                    // Notify user that PDF download is complete
                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            Toast.makeText(requireContext(), "PDF Download completed: $fileName", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Update details with PDF path and save to SharedPreferences
                    details.url = pdfFile.absolutePath // Update with PDF path
                    storeItemInPreferences(details)

                } catch (e: Exception) {
                    e.printStackTrace()
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

       lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val fileName = "$name.mp4"
                    Log.d("DownloadVideo", "File name set to: $fileName")
                    val client = OkHttpClient()
                    val request = Request.Builder().url(videoUrl).build()

                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("Failed to download file: $response")

                        val videoFile = File(context.filesDir, fileName)
                        val inputStream: InputStream? = response.body?.byteStream()
                        val outputStream = FileOutputStream(videoFile)

                        inputStream?.use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                                Log.d("DownloadVideo", "File downloaded successfully.")
                            }
                        }
                    }
                }
                // Show success toast
                withContext(Dispatchers.Main) {
                    Log.d("DownloadVideo", "Download success, showing toast.")
                    Toast.makeText(context, "Download successful", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Show error toast
                withContext(Dispatchers.Main) {
                    Log.e("DownloadVideo", "Download failed: ${e.message}")
                    Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun videoUrlApi(folderContentId: String, name: String) {
        viewModel.fetchVideoStreamUrl(folderContentId, "480p")

        viewModel.videoStreamUrl.observe(viewLifecycleOwner) { signedUrl ->
            Log.d("VideoUrl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                downloadVideo(signedUrl, name) // Download the video using the signed URL
            } else {
                // Handle error or null URL
                Toast.makeText(requireContext(), "Failed to retrieve video URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
