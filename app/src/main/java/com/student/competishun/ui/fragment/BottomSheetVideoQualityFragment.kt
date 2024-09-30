package com.student.competishun.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.student.competishun.R
import com.student.competishun.data.model.VideoQualityItem
import com.student.competishun.databinding.FragmentBottomSheetVideoQualityBinding
import com.student.competishun.ui.adapter.SelectExamAdapter
import com.student.competishun.ui.adapter.VideoQualityAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class BottomSheetVideoQualityFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentBottomSheetVideoQualityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBottomSheetVideoQualityBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val videoQualityList = listOf(
            VideoQualityItem("360p", "48.5 MB"),
            VideoQualityItem("480p", "480.5 MB"),
            VideoQualityItem("720p", "1.29 GB"),
            VideoQualityItem("1080p", "2.45 GB"),
        )

        binding.rvVideoQualityTypes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = VideoQualityAdapter(context, videoQualityList)
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
                withContext(Dispatchers.Main) {
                    Log.d("DownloadVideo", "Download success, showing toast.")
                    Toast.makeText(context, "Download successful", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("DownloadVideo", "Download failed: ${e.message}")
                    Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}