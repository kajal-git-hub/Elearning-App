package com.student.competishun.ui.fragment

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.student.competishun.R
import com.student.competishun.data.model.TopicContentModel
import com.student.competishun.databinding.FragmentBottomSheetDownloadBookmarkBinding
import com.student.competishun.ui.viewmodel.VideourlViewModel
import com.student.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@AndroidEntryPoint
class BottomSheetDownloadBookmark : BottomSheetDialogFragment() {
    private var itemDetails: TopicContentModel? = null
    private lateinit var binding : FragmentBottomSheetDownloadBookmarkBinding
    private lateinit var viewModel: VideourlViewModel
    private var pdfUrl = ""
    private var videoUrl = ""

    fun setItemDetails(details: TopicContentModel) {
        this.itemDetails = details
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
        binding = FragmentBottomSheetDownloadBookmarkBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this).get(VideourlViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.tvBookmark.setOnClickListener {

        }
        binding.tvDownload.setOnClickListener {
            itemDetails?.let { details ->
                Log.d("ItemDetails",details.toString())
//                pdfUrl = details.url
//                downloadFile(pdfUrl,details.topicName)
//                videoUrl = videoUrlApi(viewModel,details.id,details.topicName).toString()
                storeItemInPreferences(details)
                downloadFile(details)
                dismiss()
            }
        }
    }

    private fun storeItemInPreferences(item: TopicContentModel) {
        val sharedPreferencesManager = SharedPreferencesManager(requireActivity())
        sharedPreferencesManager.saveDownloadedItem(item)
    }
    private fun downloadFile(details: TopicContentModel) {
        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(details.url)
        val request = DownloadManager.Request(uri)

        request.setTitle(details.topicName)
        request.setDescription(details.topicDescription)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, details.topicName + "." + details.fileType.lowercase())
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setAllowedOverMetered(true)

        val downloadId = downloadManager.enqueue(request)
        Log.d("DownloadManager", "Download started with ID: $downloadId")

        requireContext().registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    Log.d("DownloadManager", "Download completed")
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        val filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                        Log.d("DownloadManager", "File path: $filePath")

                        // Save file path to SharedPreferences
                        details.url = filePath
                        storeItemInPreferences(details)
                    }
                    cursor.close()
                    dismiss()
                }
            }
        }, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED)
    }

    private fun videoUrlApi(viewModel: VideourlViewModel, folderContentId: String, name: String) {
        viewModel.fetchVideoStreamUrl(folderContentId, "480p")

        viewModel.videoStreamUrl.observe(viewLifecycleOwner) { signedUrl ->
            Log.d("VideoUrl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                val bundle = Bundle().apply {
                    putString("url", signedUrl)
                    putString("url_name", name)
                    putString("ContentId", folderContentId)
                }
                findNavController().navigate(R.id.mediaFragment, bundle)
            } else {
                // Handle error or null URL
            }
        }
    }

}