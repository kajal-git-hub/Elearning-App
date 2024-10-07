package xyz.penpencil.competishun.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.data.model.VideoQualityItem
import xyz.penpencil.competishun.ui.adapter.VideoQualityAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentBottomSheetVideoQualityBinding
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class BottomSheetVideoQualityFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentBottomSheetVideoQualityBinding
    private val videoUrlViewModel: VideourlViewModel by viewModels()
    private var videoUrl: String? = null
    private var videoName: String? = null
    private var videoId: String? = null
    private var qualityResolution: String? = null
    private var updateSignUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBottomSheetVideoQualityBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            videoUrl = it.getString("video_url")
            videoName = it.getString("video_name")
            videoId = it.getString("video_id")
        }
        Log.d("BottomSheetVideoQualityFragment", "Video URL: $videoUrl")
        Log.d("BottomSheetVideoQualityFragment", "Video Name: $videoName")
        Log.d("BottomSheetVideoQualityFragment", "Video ID: $videoId")

        val videoQualityList = listOf(
            VideoQualityItem("360p", "48.5 MB"),
            VideoQualityItem("480p", "480.5 MB"),
            VideoQualityItem("720p", "1.29 GB"),
            VideoQualityItem("1080p", "2.45 GB"),
        )
        binding.btnBmDownload.setOnClickListener {
            downloadVideo(requireContext(), updateSignUrl.toString(), videoName.toString())
            dismiss()
        }

        binding.rvVideoQualityTypes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = VideoQualityAdapter(context, videoQualityList){ selectedQuality ->
                qualityResolution = selectedQuality.qualityType
                videoUrlApi(videoUrlViewModel,videoId.toString(),videoName.toString())
                binding.btnBmDownload.apply {
                    setBackgroundColor(ContextCompat.getColor(context, R.color.blue_3E3EF7))
                    isEnabled = true
                }
            }
        }
    }


    private fun videoUrlApi(viewModel: VideourlViewModel, folderContentId: String, name:String) {
        viewModel.fetchVideoStreamUrl(folderContentId, qualityResolution.toString())

        viewModel.videoStreamUrl.observe(viewLifecycleOwner) { signedUrl ->
            if (signedUrl != null) {
                updateSignUrl = signedUrl
                Log.d("SignedUrl",signedUrl.toString())
            } else {
                // Handle error or null URL
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
                withContext(Dispatchers.Main) {
                    Log.d("DownloadVideo", "Download success, showing toast.")
                    Toast.makeText(context, "Download successful", Toast.LENGTH_SHORT).show()
                    val bottomSheetComplete = BottomsheetDownloadCompletedFragment()
                    val bundle = Bundle().apply {
                        putString("video_name", videoName)
                    }
                    bottomSheetComplete.arguments = bundle
                    bottomSheetComplete.show(childFragmentManager, bottomSheetComplete.tag)
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