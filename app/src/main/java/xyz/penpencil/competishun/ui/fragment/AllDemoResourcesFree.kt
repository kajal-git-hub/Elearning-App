package xyz.penpencil.competishun.ui.fragment

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.curator.FindCourseFolderProgressQuery
import com.student.competishun.curator.type.FileType
import xyz.penpencil.competishun.data.model.FreeDemoItem
import xyz.penpencil.competishun.ui.adapter.FreeDemoAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.CoursesViewModel
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import xyz.penpencil.competishun.utils.HelperFunctions
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentAllDemoResourcesFreeBinding
import xyz.penpencil.competishun.di.Result

@AndroidEntryPoint
class AllDemoResourcesFree : DrawerVisibility() {

    private var _binding: FragmentAllDemoResourcesFreeBinding? = null
    private val binding get() = _binding!!
    private val coursesViewModel: CoursesViewModel by viewModels()
    private lateinit var helperFunctions: HelperFunctions
    var isPurchased = false
    private val videourlViewModel: VideourlViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllDemoResourcesFreeBinding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }


        return binding.root
    }

    private fun handleBackPressed() {
        findNavController().navigateUp()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)


        helperFunctions = HelperFunctions()
        val folderName = arguments?.getString("folderName")
        isPurchased = arguments?.getBoolean("isPurchased") ?: false
        binding.tvDemoTitle.setOnClickListener { it.findNavController().popBackStack() }
        binding.tvDemoTitle.text = folderName
        Log.e("foldenaaamf", folderName.toString())

        if (isPurchased) {
            binding.igFreeImage.visibility = View.GONE
            if (folderName?.split(" ")?.get(0)?.equals("Class") == true) {
                binding.igFreeImage.visibility = View.VISIBLE
                binding.igFreeImage.setImageResource(R.drawable.download_banner)
            }
        } else if (!isPurchased) {
            binding.igFreeImage.visibility = View.VISIBLE
            binding.igFreeImage.setImageResource(R.drawable.lock)
        }
        if ((folderName == "") || (folderName == " ")) {
            binding.igFreeImage.visibility = View.GONE
        }

        val folderId = arguments?.getString("folderId")
        val free = arguments?.getBoolean("free")

        if (folderId != null) {
            folderProgress(folderId)

        }

    }


    fun videoUrlApi(viewModel: VideourlViewModel, folderContentId: String) {

        viewModel.fetchVideoStreamUrl(folderContentId, "360p")

        viewModel.videoStreamUrl.observe(viewLifecycleOwner) { signedUrl ->
            Log.d("Videourl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                val bundle = Bundle().apply {
                    putString("url", signedUrl)
                }
                findNavController().navigate(R.id.mediaFragment, bundle)

            } else {
                // Handle error or null URL
            }
        }
    }

    private fun folderProgress(folderId: String) {
        Log.e("folderProgress", folderId)
        //  var isPurchased = arguments?.getBoolean("isPurchased")?:false
        var free = false
        if (isPurchased) {
            free = arguments?.getBoolean("free") ?: false
        }
        if (folderId != null) {
            // Trigger the API call
            coursesViewModel.findCourseFolderProgress(folderId)
        }
        coursesViewModel.courseFolderProgress.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val data = result.data
                    if (data.findCourseFolderProgress.subfolderDurations?.isNotEmpty() == true){
                        binding.rvAllDemoFree.visibility  = View.VISIBLE
                        binding.clEmptySearchParent.visibility = View.GONE
                    }
                    Log.e("GetFolderdata", data.toString())
                    val folderProgressFolder = data.findCourseFolderProgress.folder
                    val folderProgressContent = data.findCourseFolderProgress.folderContents
                    val subfolderDurationFolders = data.findCourseFolderProgress.subfolderDurations
                    Log.e("subFolderdata", subfolderDurationFolders.toString())

                    if (folderProgressFolder != null) {
                        if (!subfolderDurationFolders.isNullOrEmpty()) {
                            Log.e("subfolderDurationszs", subfolderDurationFolders.toString())
                            getFolderList(subfolderDurationFolders)
                        } else {
                            Log.e("folderContentsss", isPurchased.toString())
                            getFileList(data, folderProgressContent, isPurchased)
                        }
                    }
                }

                is Result.Failure -> {
                    // Handle the error
                    Log.e("AllDemoResourcesFree", result.exception.message.toString())
                }

                is Result.Loading -> {
                    // Handle loading state if needed
                    Log.e("LoadingState", "Data is loading...")
                }
            }
        }


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

    private fun getFolderList(subfolderDurationFolders: List<FindCourseFolderProgressQuery.SubfolderDuration>) {
        Log.e("insidefolders", subfolderDurationFolders.toString())
        val FolderItems = subfolderDurationFolders?.map { subfolderContent ->
            val folderName = subfolderContent.folder?.name ?: ""
            val completionPercentage = "${subfolderContent.completionPercentage} perc"
            val subFolder = subfolderContent?.folder
            Log.e("infoldersname", folderName.toString())
            FreeDemoItem(
                id = subFolder?.id.toString(),
                playIcon = R.drawable.folder_bg, //static icon
                titleDemo = folderName,
                timeDemo = "",
                fileUrl = "",
                fileType = "",
                videoCount = subFolder?.video_count.toString(),
                pdfCount = subFolder?.pdf_count.toString(),
                folderCount = subFolder?.folder_count.toString()
            )
        } ?: emptyList()

        val folderDemoAdapter = FreeDemoAdapter(FolderItems) { freeDemoItem ->
            var videoCount = freeDemoItem.videoCount.toInt()
            var pdfCount = freeDemoItem.pdfCount.toInt()
            val subfolderId = freeDemoItem.id
//                 folderProgress(subfolderId)
            val bundle = Bundle().apply {
                putString("folderId", subfolderId)
                putString("folderName", "")
                putBoolean("free", false)
            }
            findNavController().navigate(R.id.demoFreeFragment, bundle)
        }
        binding.rvAllDemoFree.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = folderDemoAdapter

        }
    }

    private fun getFileList(
        data: FindCourseFolderProgressQuery.Data,
        folderProgressContent: List<FindCourseFolderProgressQuery.FolderContent>?,
        free: Boolean?
    ) {

        val folderProgressFolder = data.findCourseFolderProgress.folder
        Log.e("insideFile", folderProgressFolder.toString())
        val folderName = folderProgressFolder?.name
        val totalDuration = data.findCourseFolderProgress.videoDuration?.toInt()
            ?: 0 // Ensure totalDuration is an Int



        Log.d("totalDuration", totalDuration.toString())

        val durationString = formatTimeDuration(totalDuration)

        val freeItems = folderProgressContent?.map { folderContent ->
            val fileName = folderContent.content?.course_track ?: "Unknown"
            val videoDuration = folderContent.content?.video_duration?.toInt()
//            val duration = formatTimeDuration(durationPerContent)

            FreeDemoItem(
                id = folderContent.content?.id.toString(),
                playIcon = if (folderContent.content?.file_type == FileType.PDF) {
                    R.drawable.pdf_bg
                } else {
                    R.drawable.frame_1707480717
                }, //static icon
                titleDemo = folderContent.content?.file_name.toString(),
                timeDemo = if (folderContent.content?.file_type == FileType.PDF) {
                    ""
                } else videoDuration?.let { formatTimeDuration(it) }.toString(),
                fileUrl = folderContent.content?.file_url.toString(),
                fileType = folderContent.content?.file_type?.name.toString(),
                videoCount = "",
                pdfCount = "",
                folderCount = ""
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
            } else {
                (fileType.equals("PDF"))
                if (free == true) videoUrlApi(videourlViewModel, freeDemoItem.id)
            }
        }
        binding.rvAllDemoFree.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = freeDemoAdapter
        }
    }

    private fun formatTimeDuration(totalDuration: Int): String {
        return when {
            totalDuration < 60 -> "${totalDuration} sec"
            totalDuration == 60 -> "1h"
            else -> {
                val hours = totalDuration / 3600
                val minutes = (totalDuration % 3600) / 60
                val seconds = totalDuration % 60

                val hourString = if (hours > 0) "${hours} hr${if (hours > 1) "s" else ""}" else ""
                val minuteString =
                    if (minutes > 0) "${minutes} min${if (minutes > 1) "s" else ""}" else ""
                val secondString = if (seconds > 0) "${seconds} sec" else ""

                listOf(hourString, minuteString, secondString).filter { it.isNotEmpty() }
                    .joinToString(" ").trim()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        ContextCompat.registerReceiver(
            requireContext(),
            downloadCompleteReceiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
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
