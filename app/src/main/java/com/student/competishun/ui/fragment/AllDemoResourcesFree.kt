package com.student.competishun.ui.fragment

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.R
import com.student.competishun.curator.type.FileType
import com.student.competishun.data.model.FreeDemoItem
import com.student.competishun.ui.adapter.FreeDemoAdapter
import com.student.competishun.databinding.FragmentAllDemoResourcesFreeBinding
import com.student.competishun.ui.viewmodel.CoursesViewModel
import com.student.competishun.ui.viewmodel.VideourlViewModel
import com.student.competishun.utils.HelperFunctions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllDemoResourcesFree : Fragment() {

    private var _binding: FragmentAllDemoResourcesFreeBinding? = null
    private val binding get() = _binding!!
    private val coursesViewModel: CoursesViewModel by viewModels()
    private lateinit var helperFunctions: HelperFunctions
    private val videourlViewModel: VideourlViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllDemoResourcesFreeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helperFunctions = HelperFunctions()
        var folderName = arguments?.getString("folderName")
        binding.igDemoBackButton.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.tvDemoTitle.text = folderName
        if (folderName?.split(" ")?.get(0)?.equals("Free") == true) {
            binding.igFreeImage.visibility = View.VISIBLE
            binding.igFreeImage.setImageResource(R.drawable.frame_1707480952)
        } else {
            binding.igFreeImage.visibility = View.VISIBLE
            binding.igFreeImage.setImageResource(R.drawable.lock)
        }

        val folderId = arguments?.getString("folderId")
        val free = arguments?.getBoolean("free")
        if (folderId != null) {
            // Trigger the API call
            coursesViewModel.findCourseFolderProgress(folderId)
        }

        // Observe the API response
        coursesViewModel.courseFolderProgress.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                Log.e("GetFolderdata", data.toString())
                val folderProgressFolder = data.findCourseFolderProgress.folder
                val folderProgressContent = data.findCourseFolderProgress.folderContents
                val subfolderDurationFolders = data.findCourseFolderProgress.subfolderDurations
                Log.e("subFolderdata", subfolderDurationFolders.toString())
                if (folderProgressFolder != null) {

                 if (!subfolderDurationFolders.isNullOrEmpty()){
                     Log.e("insidefolders",subfolderDurationFolders.toString())
                     val FolderItems = subfolderDurationFolders?.map { subfolderContent ->
                         val folderName = subfolderContent.folder?.name ?: ""
                         val completionPercentage = "${subfolderContent.completionPercentage} perc"
                         val subFolder = subfolderContent?.folder
                         Log.e("infoldersname",folderName.toString())
                         FreeDemoItem(
                             id = subFolder?.id.toString(),
                             playIcon =  R.drawable.frame_1707480717, //static icon
                             titleDemo = subFolder?.name.toString(),
                             timeDemo =   "",
                             fileUrl = "",
                             fileType = ""
                         )
                     } ?: emptyList()
                 }

                    val folderName = folderProgressFolder.name

                    val totalDuration = data.findCourseFolderProgress.videoDuration ?: 0.0
                    // Calculate duration per item or use a static value
                    val contentCount = folderProgressContent?.size ?: 0
                    val durationPerContent = if (contentCount > 0) totalDuration / contentCount else 0.0
                    // Map folderContents to FreeDemoItem
                    val freeItems = folderProgressContent?.map { folderContent ->
                        val fileName = folderContent.content?.course_track ?: "Unknown"
                        val duration = "$durationPerContent mins"

                        FreeDemoItem(
                            id = folderContent.content?.id.toString(),
                            playIcon = if(folderContent.content?.file_type == FileType.PDF ){ R.drawable.frame_1707480717} else {R.drawable.frame_1707480717}, //static icon
                            titleDemo = folderContent.content?.file_name.toString(),
                            timeDemo = if(folderContent.content?.file_type == FileType.PDF ){ ""} else {duration},
                            fileUrl = folderContent.content?.file_url.toString(),
                            fileType = folderContent.content?.file_type?.name.toString()
                        )
                    } ?: emptyList()
                        val freeDemoAdapter = FreeDemoAdapter(freeItems) { freeDemoItem ->
                            // Handle the item click
                            val fileType = freeDemoItem.fileType
                            if (fileType.equals("PDF")) {
                             if (free == true)
                                helperFunctions.showDownloadDialog(
                                    requireContext(),
                                    freeDemoItem.fileUrl,
                                    freeDemoItem.titleDemo
                                )
                            } else if(!subfolderDurationFolders.isNullOrEmpty()){

                                Log.e("fodername id ${freeDemoItem.id}",freeDemoItem.titleDemo)
                                val bundle = Bundle().apply {
                                    putString("folderId", freeDemoItem.id)
                                    putString("folderName", freeDemoItem.titleDemo)
                                }
                                findNavController().navigate(R.id.action_exploreFragment_to_demoFreeFragment, bundle)
                            }
                            else
                            { (fileType)
                                if (free == true) videoUrlApi(videourlViewModel, freeDemoItem.id)
                            }
                        }
                        binding.rvAllDemoFree.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = freeDemoAdapter

                    }
                }
            }.onFailure { error ->
                // Handle the error
                Log.e("AllDemoResourcesFree", error.message.toString())
            }
        }

    }


    fun videoUrlApi(viewModel:VideourlViewModel,folderContentId:String){

        viewModel.fetchVideoStreamUrl(folderContentId, "360p")

        viewModel.videoStreamUrl.observe(viewLifecycleOwner, { signedUrl ->
            Log.d("Videourl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                val bundle = Bundle().apply {
                    putString("url", signedUrl)
                }
                findNavController().navigate(R.id.mediaFragment,bundle)

            } else {
                // Handle error or null URL
            }
        })
    }



    private val downloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id != -1L) {
                // Notify the user within the app that the download is complete, e.g., show a Toast or update the UI
                Toast.makeText(context, "Download complete!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        requireContext().registerReceiver(
            downloadCompleteReceiver,
            intentFilter,
            Context.RECEIVER_NOT_EXPORTED // This ensures the receiver is not available to other apps
        )
    }

    override fun onPause() {
        super.onPause()
        // Unregister the BroadcastReceiver
        requireContext().unregisterReceiver(downloadCompleteReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
