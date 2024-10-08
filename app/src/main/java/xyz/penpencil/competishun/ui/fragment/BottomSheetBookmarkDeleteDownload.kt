package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.databinding.FragmentBottomSheetBookmarkDeleteDownloadBinding
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


@AndroidEntryPoint
class BottomSheetBookmarkDeleteDownload(
    private val listener: OnDeleteItemListener,
) : BottomSheetDialogFragment() {
    private var position: Int = -1
    private lateinit var item: TopicContentModel
    private lateinit var binding : FragmentBottomSheetBookmarkDeleteDownloadBinding


    fun setItem(position: Int, item: TopicContentModel) {
        this.position = position
        this.item = item
    }

    interface OnDeleteItemListener {
        fun onDeleteItem(position: Int,item: TopicContentModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBookmarkDeleteDownloadBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(item.fileType=="PDF"){
            binding.tvBmDownloadThing.setOnClickListener {
                downloadPdf(item)
            }
        }else{
            binding.tvBmDownloadThing.setOnClickListener {
                val bottomSheetDownloadOptions = BottomSheetVideoQualityFragment()

                val bundle  = Bundle().apply {
                    putString("video_url",item.url)
                    putString("video_name",item.topicName)
                    putString("video_id",item.id)
                }
                bottomSheetDownloadOptions.arguments = bundle

                bottomSheetDownloadOptions.show(childFragmentManager, bottomSheetDownloadOptions.tag)
            }
        }

        binding.tvBmDeleteThing.setOnClickListener {
            listener.onDeleteItem(position,item)
            dismiss()
        }

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
                    storeItemInPreferencesBm(details)
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
    private fun storeItemInPreferencesBm(item: TopicContentModel) {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        sharedPreferencesManager.saveDownloadedItemBm(item)
    }


}