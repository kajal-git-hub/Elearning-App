package xyz.penpencil.competishun.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.Player
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.student.competishun.curator.GetCourseByIdQuery
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.CourseFItem
import xyz.penpencil.competishun.data.model.SubjectContentItem
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.data.model.TopicTypeModel
import xyz.penpencil.competishun.databinding.FragmentStudyMaterialBinding
import xyz.penpencil.competishun.databinding.FragmentStudyMaterialDetailsBinding
import xyz.penpencil.competishun.di.Result
import xyz.penpencil.competishun.di.SharedVM
import xyz.penpencil.competishun.ui.adapter.CourseFeaturesAdapter
import xyz.penpencil.competishun.ui.adapter.SubjectContentAdapter
import xyz.penpencil.competishun.ui.adapter.TopicContentAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.main.PdfViewActivity
import xyz.penpencil.competishun.ui.viewmodel.CoursesViewModel
import xyz.penpencil.competishun.ui.viewmodel.GetCourseByIDViewModel
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.OnTopicTypeSelectedListener
import xyz.penpencil.competishun.utils.SharedPreferencesManager

@AndroidEntryPoint
class StudyMaterialDetailsFragment : Fragment() {
    private lateinit var helperFunctions: HelperFunctions
    private lateinit var courseId: String
    private val videourlViewModel: VideourlViewModel by viewModels()
    private val coursesViewModel: CoursesViewModel by viewModels()
    lateinit var folderlist: List<GetCourseByIdQuery.Folder>
    var VwatchedDuration: Int = 0
    private lateinit var sharedViewModel: SharedVM
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()
    private lateinit var binding : FragmentStudyMaterialDetailsBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudyMaterialDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.showBottomNavigationView(true)
        (activity as? HomeActivity)?.showFloatingButton(true)

        helperFunctions = HelperFunctions()
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        binding.backIcon.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.rvSubjectContent.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvStudyMaterial.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        courseId = arguments?.getString("course_id").toString()
        arguments?.let { bundle ->
            val tags = bundle.getStringArrayList("course_tags")
            val recommendCourseTags = bundle.getStringArrayList("recommendCourseTags")
            val bannerCourseTags = bundle.getStringArrayList("bannerCourseTag")

            Log.d("recommendCourseTags", recommendCourseTags.toString())
            Log.d("tags", tags.toString())
            Log.d("bannerCourseTags", bannerCourseTags.toString())
            binding.progressBar.visibility = View.VISIBLE
            binding.clStudyMaterialDetailsView.visibility = View.GONE
            getCourseByIDViewModel.fetchCourseById(courseId)
            getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner, Observer { courses ->

                Log.d("courseDetail",courses.toString())
                val imageUrl = courses?.video_thumbnail
                val videoUrl = courses?.orientation_video
                folderlist = courses?.folder ?: emptyList()
                Log.d("CourseVideoThumbnail", imageUrl ?: "No URL")
                Log.d("CourseOrientThumbnail", videoUrl ?: "No URL")
                Log.d("folderLists", folderlist.toString())
                courses?.let {
                    binding.progressBar.visibility = View.GONE
                    binding.clStudyMaterialDetailsView.visibility = View.VISIBLE
                    val totalPdfCount = courses.folder?.sumOf { folder ->
                        folder.pdf_count?.toIntOrNull() ?: 0
                    } ?: 0
                    binding.tvNoOfPdf.text = "${totalPdfCount.toString()} Subjects"
                }
                if (folderlist[0].id != null) {
                    var id = folderlist[0].id ?: ""
                    var name = folderlist[0].id ?: ""
                    binding.tvTopicType.text = folderlist[0].name
                    folderProgress(id)
                    // isFirstTimeLoading = false
                }
                val gson = Gson()
                val folderlistJson = gson.toJson(folderlist)
                binding.clTopicType.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("subFolders", folderlistJson)
                        Log.e("foldernames", folderlistJson)
                        putString("folder_Count", folderlist.size.toString())
                    }
                    Log.e("clickevent", folderlistJson.toString())


                    val bottomSheet = BottomsheetCourseSubjectsFragment().apply {
                        arguments = bundle
                    }

                    bottomSheet.setOnTopicTypeSelectedListener(object :
                        OnTopicTypeSelectedListener {
                        override fun onTopicTypeSelected(selectedTopic: TopicTypeModel) {
                            selectedTopic
                            binding.tvTopicType.text = selectedTopic.title

                            folderProgress(selectedTopic.id)
                        }
                    })
                    bottomSheet.show(
                        childFragmentManager,
                        "BottomsheetCourseSubjectsFragment"
                    )


                }

                Glide.with(requireContext())
                    .load(imageUrl)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .into(binding.ivSubjectBookIcon)
                binding.tvTopicDesc.text = courses?.description ?:""

               // binding.progressBar.visibility = View.GONE
                if (courses?.planner_pdf != null) {
                    binding.clBtnDownloadFolder.setOnClickListener {

                        Log.d("planner_pdf", courses.planner_pdf)
                        helperFunctions.showDownloadDialog(requireContext(),courses.planner_pdf,"Planner")

                    }
                }
                else Toast.makeText(requireContext(), "", Toast.LENGTH_LONG).show()

                if (courses != null) {
                    var coursefeature = courses.course_features

                    folderlist = courses.folder ?: emptyList()
                    val sortedFolderList = folderlist.sortedByDescending {
                        it.name.startsWith("Class")
                    }

                    binding.tvTopicName.text = courses.name
                    val categoryName = courses.category_name?.split(" ") ?: emptyList()

                    val freeCourse = courses.folder?.get(0)?.name?.split(" ")?.get(0)
                    Log.e("getFreeCourse", freeCourse.toString())

                }

            })
        }

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedVM::class.java)
        sharedViewModel.watchedDuration.observe(viewLifecycleOwner, Observer { duration ->
            VwatchedDuration = duration
            Log.d("SubjectContentFragment", "Received duration: $duration")
            // Update UI or perform actions based on the received duration
        })
        }

    private fun folderProgress(folderId: String) {
        Log.e("folderProgress", folderId)
        val free = arguments?.getBoolean("free")

        if (folderId.isNotEmpty()) {
            // Trigger the API call
            coursesViewModel.findCourseFolderProgress(folderId)
        }

        coursesViewModel.courseFolderProgress.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    // Handle the loading state
                    Log.d("folderProgress", "Loading...")
                    // Optionally show a loading indicator here
                }

                is Result.Success -> {
                    val data = result.data
                    Log.e("GetFolderdata", data.toString())
                    Log.e("findCoursgress", data.findCourseFolderProgress.folder.toString())
                    val folderProgressFolder = data.findCourseFolderProgress
                    val folderProgressContent = data.findCourseFolderProgress.folderContents
                    val subfolderDurationFolders = data.findCourseFolderProgress.subfolderDurations
                    Log.e("subFolderdata", subfolderDurationFolders.toString())

                    // Clear previous adapter to prevent issues
//                    binding.rvSubjectContent.adapter = null
//                    binding.rvTopicContent.adapter = null

                    if (folderProgressFolder != null) {
                        if (!subfolderDurationFolders.isNullOrEmpty()) {
                            // Process subfolder durations
                            Log.e("subfolderDurationszs", subfolderDurationFolders.toString())

                        }
                    }

                    binding.rvSubjectContent.adapter = null
                    binding.rvStudyMaterial.adapter = null

                    when {
                        !subfolderDurationFolders.isNullOrEmpty() && !folderProgressContent.isNullOrEmpty() ->
                        {
                              binding.tvContentCount.text = "(${folderProgressContent.size})"
                            val topicContentList =
                                folderProgressContent.mapIndexed { index, contents ->
                                    Log.e("folderContentLog", contents.content?.file_url.toString())
                                    Log.e("homeworkLog", contents.content?.homework.toString())
                                    val time = helperFunctions.formatCourseDateTime("2024-10-11T17:27:00.000Z")
                                    Log.e("foldertimes", time)

                                    // Extract homework URL and filename if they exist
                                    val homeworkUrl = contents.content?.homework?.map { it.file_url?:"" } ?:""
                                    val homeworkFileName = contents.content?.homework?.map { it.file_name?:"" } ?: ""

                                    TopicContentModel(
                                        subjectIcon = if (contents.content?.file_type?.name == "PDF") R.drawable.content_bg else R.drawable.group_1707478994,
                                        id = contents.content?.id ?: "",
                                        playIcon = if (contents.content?.file_type?.name == "VIDEO") R.drawable.video_bg else 0,
                                        lecture = if (contents.content?.file_type?.name == "VIDEO") "Lecture" else "Study Material",
                                        lecturerName = if(contents.content?.file_type?.name == "VIDEO") formatTimeDuration(
                                            contents.content.video_duration ?: 0
                                        ) else "Ashok",
                                        topicName = contents.content?.file_name ?: "",
                                        topicDescription = contents.content?.description?:"",
                                        progress = 1,
                                        videoDuration = contents.content?.video_duration ?: 0,
                                        url = contents.content?.file_url.toString(),
                                        fileType = contents.content?.file_type?.name ?: "",
                                        lockTime = time,
                                        // Assign homework name and URL here
                                        homeworkName = homeworkFileName.toString(),
                                        homeworkUrl = homeworkUrl.toString()  // Add this field in your TopicContentModel if it doesn't exist
                                    )
                                }
                            val folderContentIds = folderProgressContent.mapNotNull { it.content?.id }.toCollection(ArrayList())
                            val folderContentNames = folderProgressContent.mapNotNull { it.content?.file_name }.toCollection(ArrayList())
                            Log.e("getfoldersubject2",folderContentNames.toString())
                            binding.rvStudyMaterial.adapter = TopicContentAdapter(
                                topicContentList,
                                folderId,
                                requireActivity(),
                                requireContext()
                            ) { topicContent, folderContentId ->
                                when (topicContent.fileType) {
                                    "VIDEO" -> {
                                        videoUrlApi(videourlViewModel, topicContent.id,topicContent.topicName,folderContentIds,folderContentNames)
                                    }
                                    "PDF" -> {
                                        val intent = Intent(context, PdfViewActivity::class.java).apply {
                                            putExtra("PDF_URL", topicContent.url)
                                            putExtra("PDF_TITLE",topicContent.topicName)
                                        }
                                        context?.startActivity(intent)
                                    }
                                    "FOLDER" -> {
                                        Log.e("typeofget",topicContent.fileType)
                                    }
                                    else -> {
                                        Log.d(
                                            "TopicContentAdapter",
                                            "File type is not recognized: ${topicContent.fileType}"
                                        )
                                    }
                                }

                            }

                            Log.e("subfolderDurationszs", subfolderDurationFolders.toString())
                            val folderIds = subfolderDurationFolders.mapNotNull { it.folder?.id }
                            val folders =
                                subfolderDurationFolders.mapNotNull { it.folder }

                            val folderCounts =
                                subfolderDurationFolders.mapNotNull { it.folder?.folder_count ?: 0 }
                            val scheduledTimes = subfolderDurationFolders?.mapNotNull {
                                it.completionPercentage
                            }



                            val subjectContentList = folders.mapIndexed { index, folders ->
                                Log.e("folderContentLog", folders.id)
                                val id = folders.id
                                val date = folders.scheduled_time.toString()
                                val time = helperFunctions.formatCourseDateTime("2024-10-11T17:27:00.000Z")
                                SubjectContentItem(
                                    id = id,
                                    chapterNumber = index + 1,
                                    topicName = folders.name?:"",
                                    topicDescription = folders.folder_count?:"0",
                                    locktime = time,
                                    progressPer = subfolderDurationFolders[index].completionPercentage.toInt()
                                )
                            }

                            binding.rvSubjectContent.adapter =
                                SubjectContentAdapter(subjectContentList) { selectedItem ->
                                    videoProgress(
                                        selectedItem.id,
                                       currentDuration = VwatchedDuration
                                    )
                                   FileProgress(selectedItem.id,selectedItem.topicName,"")

//
                                }

                        }

                        !subfolderDurationFolders.isNullOrEmpty() -> {
                            Log.e("subfolderDurationszs", subfolderDurationFolders.toString())
                            val folderIds = subfolderDurationFolders.mapNotNull { it.folder?.id }
                            val folders =
                                subfolderDurationFolders.mapNotNull { it.folder }

                            val folderCounts =
                                subfolderDurationFolders.mapNotNull { it.folder?.folder_count ?: 0 }
                            val scheduledTimes = subfolderDurationFolders?.mapNotNull {
                                it.completionPercentage
                            }



                            val subjectContentList = folders.mapIndexed { index, folders ->
                                Log.e("folderContentLog", folders.id)
                                val id = folders.id
                                val date = folders.scheduled_time.toString()
                                Log.e("foldertimes", date)
                                val time = helperFunctions.formatCourseDateTime("2024-10-11T17:27:00.000Z")

                                SubjectContentItem(
                                    id = id,
                                    chapterNumber = index + 1,
                                    topicName = folders.name,
                                    topicDescription = folders.folder_count?:"0",
                                    locktime = time,
                                    progressPer = subfolderDurationFolders[index].completionPercentage.toInt()
                                )
                            }
                            binding.rvSubjectContent.adapter =
                                SubjectContentAdapter(subjectContentList) { selectedItem ->
                                    videoProgress(
                                        selectedItem.id,
                                        currentDuration = VwatchedDuration
                                    )
                                    FileProgress(selectedItem.id,selectedItem.topicName,"")


                               }

                        }


                        !folderProgressContent.isNullOrEmpty() -> {
                            Log.e("folderContentsss", folderProgressContent.toString())
                            binding.tvContentCount.text = "(${folderProgressContent.size})"

                            val subjectContentList =
                                folderProgressContent.mapIndexed { index, contents ->
                                    Log.e("folderContentLog", contents.content?.file_url.toString())

                                    val homeworkUrl = contents.content?.homework?.map { it.file_url } ?:""
                                    val homeworkFileName = contents.content?.homework?.map { it.file_name } ?: ""


                                    val time = helperFunctions.formatCourseDateTime("2024-10-11T17:27:00.000Z")
                                    Log.e("foldertime", time)
                                    TopicContentModel(
                                        subjectIcon = if (contents.content?.file_type?.name == "PDF") R.drawable.content_bg else R.drawable.group_1707478994,
                                        id = contents.content?.id ?: "",
                                        playIcon = if (contents.content?.file_type?.name == "VIDEO") R.drawable.video_bg else 0,
                                        lecture = if (contents.content?.file_type?.name == "VIDEO") "Lecture" else "Study Material",
                                        lecturerName = if(contents.content?.file_type?.name == "VIDEO") formatTimeDuration(
                                            contents.content.video_duration ?: 0
                                        ) else "Ashok" ,
                                        topicName = contents.content?.file_name ?: "",
                                        topicDescription = contents.content?.description?:"",
                                        progress = 1,
                                        videoDuration = contents.content?.video_duration
                                            ?: 0,
                                        url = contents.content?.file_url.toString(),
                                        fileType = contents.content?.file_type?.name ?: "",
                                        lockTime =  time,
                                        homeworkUrl = homeworkUrl.toString(),
                                        homeworkName = homeworkFileName.toString()
                                    )
                                }
                            binding.tvContentCount.text = "(${subjectContentList.size + (folderProgressContent?.size ?: 0)})"
                            val folderContentIds = folderProgressContent.mapNotNull { it.content?.id }.toCollection(ArrayList())
                            val folderContentNames = folderProgressContent.mapNotNull { it.content?.file_name }.toCollection(ArrayList())
                            Log.e("getfoldersubject1",folderContentNames.toString())
                            binding.rvStudyMaterial.adapter = TopicContentAdapter(
                                subjectContentList,
                                folderId,
                                requireActivity(),
                                requireContext()
                            ) { topicContent, folderContentId ->
                                when (topicContent.fileType) {

                                    "VIDEO" -> {
                                        videoUrlApi(videourlViewModel, topicContent.id,topicContent.topicName,folderContentIds,folderContentNames)
                                    }
                                    "PDF" -> {
                                        val intent = Intent(context, PdfViewActivity::class.java).apply {
                                            putExtra("PDF_URL", topicContent.url)
                                            putExtra("PDF_TITLE",topicContent.topicName)
                                        }
                                        context?.startActivity(intent)
                                    }
                                    "FOLDER" -> {
                                        Log.e("typeofget",topicContent.fileType)
                                    }
                                    else -> {
                                        Log.d(
                                            "TopicContentAdapter",
                                            "File type is not recognized: ${topicContent.fileType}"
                                        )
                                    }
                                }

                            }
                        }

                        else -> {
                            Log.e("folderContentsss", "No content available")
                            binding.tvContentCount.text = "(0)"
                         //   binding.rvSubjectContent.adapter = null
                        }
                    }
                }

                is Result.Failure -> {
                    // Handle the error
                    Log.e("AllDemoResourcesFree", result.exception.message.toString())
                    // Optionally show an error message or indicator
                }

                else -> {
                    Log.e("folderProgress", "Unexpected result type: $result")
                }
            }
        }
    }

    fun videoProgress(courseFolderContentId: String, currentDuration: Int) {


        // Observe the result of the updateVideoProgress mutation
        videourlViewModel.updateVideoProgressResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Log.e("video progress update","successfully")
            } else {
                Log.e("video progress update","failed")

            }
        }

        // Call the mutation
        videourlViewModel.updateVideoProgress(courseFolderContentId, currentDuration)

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
                val minuteString = if (minutes > 0) "${minutes} min${if (minutes > 1) "s" else ""}" else ""
                val secondString = if (seconds > 0) "${seconds} sec" else ""

                listOf(hourString, minuteString, secondString).filter { it.isNotEmpty() }.joinToString(" ").trim()
            }
        }
    }
    private fun FileProgress(folderId: String, folderNames: String,folderCount: String){

        Log.e("foldessss $folderNames",folderId)
        val free = arguments?.getBoolean("free")
        if (folderId != null) {
            // Trigger the API call
            coursesViewModel.findCourseFolderProgress(folderId)
        }
        coursesViewModel.courseFolderProgress.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val data = result.data
                    Log.e("GetFolderdataa", data.toString())
                    Log.e("GetFolders", data.findCourseFolderProgress.folder.toString())

                    val folderProgressFolder = data.findCourseFolderProgress.folder
                    val folderProgressContent = data.findCourseFolderProgress.folderContents
                    val subfolderDurationFolders = data.findCourseFolderProgress.subfolderDurations
                    Log.e("subFolderdata", subfolderDurationFolders.toString())
                    val name = folderNames // Ensure folderNames is correctly assigned before using

                    if (folderProgressFolder != null) {
                        if (!subfolderDurationFolders.isNullOrEmpty()) {
                            // Process subfolder durations
                            Log.e("subfolderDurationszs", subfolderDurationFolders.toString())

                            val gson = Gson()
                            val subFoldersJson = gson.toJson(subfolderDurationFolders)
                            Log.e("folderNamesss $name", subFoldersJson)

                            val bundle = Bundle().apply {
                                putString("subFolders", subFoldersJson)
                                putString("folder_Name", name)
                            }
                            findNavController().navigate(R.id.SubjectContentFragment, bundle)
                        } else if (!folderProgressContent.isNullOrEmpty()) {
                            // Process folder contents
                            Log.e("folderContentsss", folderProgressContent.toString())

                            val gson = Gson()
                            val folderContentsJson = gson.toJson(folderProgressContent)

                            val bundle = Bundle().apply {

                                putString("folderContents", folderContentsJson)
                                putString("folder_Id", folderId)
                                putString("folderName", folderNames)
                                putString("studyMaterial", "studyMaterial")

                            }
                            findNavController().navigate(R.id.TopicTYPEContentFragment, bundle)
                        }
                    }
                }
                is Result.Failure -> {
                    // Handle errors
                    Log.e("AllDemoResourcesFree", result.exception.message.toString())
                }
                is Result.Loading -> {
                    // Optionally handle loading state
                    Log.e("LoadingState", "Data is loading...")
                }
            }
        }


    }
    private fun videoUrlApi(viewModel: VideourlViewModel, folderContentId: String, name:String, folderContentIds: ArrayList<String>?, folderContentNames: ArrayList<String>?) {
        Log.e("getfoldersubject",folderContentNames.toString())
        viewModel.fetchVideoStreamUrl(folderContentId, "480p")

        viewModel.videoStreamUrl.observe(viewLifecycleOwner) { signedUrl ->
            Log.d("VideoUrl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                val bundle = Bundle().apply {
                    putString("url", signedUrl)
                    putString("url_name", name)
                    putString("ContentId", folderContentId)
                    putStringArrayList("folderContentIds", folderContentIds)
                    putStringArrayList("folderContentNames", folderContentNames)
                }
                findNavController().navigate(R.id.mediaFragment, bundle)

            } else {
                // Handle error or null URL
            }
        }
    }

    }