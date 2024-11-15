package xyz.penpencil.competishun.ui.fragment

import android.content.Context
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.student.competishun.curator.GetCourseByIdQuery
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.SubjectContentItem
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.data.model.TopicTypeModel
import xyz.penpencil.competishun.databinding.FragmentYTCourseDetailsBinding
import xyz.penpencil.competishun.di.Result
import xyz.penpencil.competishun.ui.adapter.SubjectContentAdapter
import xyz.penpencil.competishun.ui.adapter.TopicContentAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.main.PdfViewActivity
import xyz.penpencil.competishun.ui.main.YoutubeActivity
import xyz.penpencil.competishun.ui.viewmodel.CoursesViewModel
import xyz.penpencil.competishun.ui.viewmodel.GetCourseByIDViewModel
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import xyz.penpencil.competishun.utils.BindingAdapters
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.OnTopicTypeSelectedListener
import xyz.penpencil.competishun.utils.SharedPreferencesManager

@AndroidEntryPoint
class YTCourseDetailsFragment : Fragment() {

    private lateinit var binding: FragmentYTCourseDetailsBinding
    private lateinit var helperFunctions: HelperFunctions
    private lateinit var courseId: String
    private val videourlViewModel: VideourlViewModel by viewModels()
    private val coursesViewModel: CoursesViewModel by viewModels()
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    lateinit var folderlist: List<GetCourseByIdQuery.Folder>
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       binding = FragmentYTCourseDetailsBinding.inflate(inflater,container,false)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false
        )
        helperFunctions = HelperFunctions()
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        binding.backIcon.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.rvYTCourseVideoListing.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvYTVideoListing.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        courseId = arguments?.getString("course_id").toString()
        arguments?.let { bundle ->
            val tags = bundle.getStringArrayList("course_tags")
            val recommendCourseTags = bundle.getStringArrayList("recommendCourseTags")
            val bannerCourseTags = bundle.getStringArrayList("bannerCourseTag")

            Log.d("recommendCourseTags", recommendCourseTags.toString())
            Log.d("tags", tags.toString())
            Log.d("bannerCourseTags", bannerCourseTags.toString())
           // binding.progressBar.visibility = View.VISIBLE
            binding.clYTCourseBannerView.visibility = View.GONE
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
                   // binding.progressBar.visibility = View.GONE
                    binding.clYTCourseBannerView.visibility = View.VISIBLE
                    val totalPdfCount = courses.folder?.sumOf { folder ->
                        folder.video_count?.toIntOrNull() ?: 0
                    } ?: 0
                }
                if (folderlist[0].id != null) {
                    var id = folderlist[0].id ?: ""
                    var name = folderlist[0].id ?: ""
                    val data = folderlist[0].name
                    Log.e("mndbnmbmasbmda", "${sharedPreferencesManager.hasKey("TOPIC_ID_STUDY")}: "+data)
                    binding.tvTopicType.text = folderlist[0].name
                    if (sharedPreferencesManager.hasKey("TOPIC_ID_STUDY") && !sharedPreferencesManager.getString("TOPIC_ID_STUDY", "").isNullOrEmpty()){
                        binding.tvTopicType.text = sharedPreferencesManager.getString("TOPIC_ID_STUDY_TYPE", "Physics")

                        Log.e("JJGHJGJGHJGy", "FOLDER_NAME: ${binding.tvTopicType.text}")
                        folderProgress(sharedPreferencesManager.getString("TOPIC_ID_STUDY", "")?:id)
                    }else{
                        sharedPreferencesManager.putString("TOPIC_ID_YT_TYPE", data)
                        folderProgress(id)
                    }
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
                            binding.tvTopicType.text = selectedTopic.title
                            sharedPreferencesManager.putString("TOPIC_ID_STUDY", selectedTopic.id)
                            sharedPreferencesManager.putString("TOPIC_ID_YT_TYPE", selectedTopic.title.toString())
                            Log.e("getaindfd",selectedTopic.title.toString())

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
                    .into(binding.ivYtCourseBanner)
                binding.tvYtCourseName.text = courses?.name?:""
                Log.e("descroptions",courses?.description.toString())
               // BindingAdapters.setTextHtml(binding.tvTopicDesc,courses?.description)
                // binding.progressBar.visibility = View.GONE
                if (courses?.planner_pdf != null) {
//                    binding.clBtnDownloadFolder.setOnClickListener {
//
//                        Log.d("planner_pdf", courses.planner_pdf)
//                        helperFunctions.showDownloadDialog(requireContext(),courses.planner_pdf,"Planner")
//
//                    }
                }
                else Toast.makeText(requireContext(), "", Toast.LENGTH_LONG).show()

                if (courses != null) {
                    var coursefeature = courses.course_features

                    folderlist = courses.folder ?: emptyList()
                    val sortedFolderList = folderlist.sortedByDescending {
                        it.name.startsWith("Class")
                    }

                    binding.tvYtCourseName.text = courses.name
                    val categoryName = courses.category_name?.split(" ") ?: emptyList()

                    val freeCourse = courses.folder?.get(0)?.name?.split(" ")?.get(0)
                    Log.e("getFreeCourse", freeCourse.toString())

                }

            })
        }

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
                    val size = folderProgressContent?.filter { it.content?.file_type?.name  == "URL" }?.mapNotNull { it.content?.file_url }?.size?:"0"

                    binding.tvNoOfVideos.text = "${size} Vidoes"
                    // Clear previous adapter to prevent issues
//                    binding.rvSubjectContent.adapter = null
//                    binding.rvTopicContent.adapter = null

                    if (folderProgressFolder != null) {
                        if (!subfolderDurationFolders.isNullOrEmpty()) {
                            // Process subfolder durations
                            Log.e("subfolderDurationszs", subfolderDurationFolders.toString())

                        }
                    }

                    binding.rvYTCourseVideoListing.adapter = null
                    binding.rvYTVideoListing.adapter = null

                    when {
                        !subfolderDurationFolders.isNullOrEmpty() && !folderProgressContent.isNullOrEmpty() ->
                        {
                           // binding.tvContentCount.text = "(${folderProgressContent.size})"
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
                                        ) else "",
                                        topicName = contents.content?.file_name ?: "",
                                        topicDescription = contents.content?.description?:"",
                                        progress = 1,
                                        videoDuration = contents.content?.video_duration ?: 0,
                                        url = contents.content?.file_url.toString(),
                                        fileType = contents.content?.file_type?.name ?: "",
                                        lockTime = time,
                                        homeworkDesc = (contents.content?.homework?.map { it.description?:"" } ?:"").toString(),
                                        // Assign homework name and URL here
                                        homeworkName = homeworkFileName.toString(),
                                        homeworkUrl = homeworkUrl.toString(), // Add this field in your TopicContentModel if it doesn't exist
                                        isExternal = contents.content?.file_name?.contains("DPPs") == true
                                    )
                                }
                            val folderContentIs = folderProgressContent.filter { it.content?.file_type?.name  == "VIDEO" }.mapNotNull { it.content?.id }.toCollection(ArrayList())
                            val folderContentNmes = folderProgressContent.filter { it.content?.file_type?.name  == "URL" }.mapNotNull { it.content?.file_name }.toCollection(ArrayList())
                            Log.e("getfoldersubject2",folderContentNmes.toString())
                            binding.rvYTVideoListing.adapter = TopicContentAdapter(
                                topicContentList.toMutableList(),
                                folderId,
                                binding.tvTopicType.text.toString(),
                                requireActivity(),
                                requireContext()
                            ) { topicContent, folderContentId, folderContentIds,folderContentNames, folderContentDescs,homework,links,des->
                                when (topicContent.fileType) {
                                    "VIDEO" -> {
                                     //   videoUrlApi(videourlViewModel, topicContent.id,topicContent.topicName,folderContentIds,folderContentNames,folderContentDescs)
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
                                    "URL" -> {
                                        Log.e("typeofget", topicContent.fileType)
                                        goToPlayerPage(requireContext(), topicContent.url, "About this Course")
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
                                    topicDescription = folders.description?:"",
                                    pdfcount = folders.pdf_count?:"0",
                                    videocount = folders.video_count?:"0",
                                    locktime = time,
                                    progressPer = subfolderDurationFolders[index].completionPercentage.toInt()
                                )
                            }

                            binding.rvYTCourseVideoListing.adapter =
                                SubjectContentAdapter(subjectContentList, binding.tvTopicType.text.toString()) { selectedItem ->
                                    Log.e("gettingcontenList",subjectContentList.toString())
                                    binding.tvTopicType.text = selectedItem.topicName
                             //       binding.tvContentCount.text = selectedItem.topicDescription
                                    FileProgress(selectedItem.id,selectedItem.topicName,"")

//
                                }

                        }

                        !subfolderDurationFolders.isNullOrEmpty() -> {
                            Log.e("folderDataDurationszs", subfolderDurationFolders.toString())
                            val folderIds = subfolderDurationFolders.mapNotNull { it.folder?.id }
                            val folders =
                                subfolderDurationFolders.mapNotNull { it.folder }

                            val folderCounts =
                                subfolderDurationFolders.mapNotNull { it.folder?.folder_count ?: 0 }
                            val scheduledTimes = subfolderDurationFolders?.mapNotNull {
                                it.completionPercentage
                            }



                            val subjectContentList = folders.mapIndexed { index, folders ->
                                Log.e("foldersDataLog", folders.id)
                                val id = folders.id
                                val date = folders.scheduled_time.toString()
                                Log.e("foldertimes", date)
                                val time = helperFunctions.formatCourseDateTime("2024-10-11T17:27:00.000Z")

                                SubjectContentItem(
                                    id = id,
                                    chapterNumber = index + 1,
                                    topicName = folders.name,
                                    topicDescription = folders.description?:"",
                                    pdfcount = folders.pdf_count?:"0",
                                    videocount = folders.video_count?:"0",
                                    locktime = time,
                                    progressPer = subfolderDurationFolders[index].completionPercentage.toInt()
                                )
                            }
                            binding.rvYTCourseVideoListing.adapter =
                                SubjectContentAdapter(subjectContentList, binding.tvTopicType.text.toString()) { selectedItem ->
                                    FileProgress(selectedItem.id,selectedItem.topicName,"")


                                }

                        }


                        !folderProgressContent.isNullOrEmpty() -> {
                            Log.e("folderContentsss", folderProgressContent.toString())
                    //        binding.tvContentCount.text = "(${folderProgressContent.size})"

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
                                        ) else "" ,
                                        topicName = contents.content?.file_name ?: "",
                                        topicDescription = contents.content?.description?:"",
                                        progress = 1,
                                        videoDuration = contents.content?.video_duration
                                            ?: 0,
                                        url = contents.content?.file_url.toString(),
                                        fileType = contents.content?.file_type?.name ?: "",
                                        lockTime =  time,
                                        homeworkUrl = homeworkUrl.toString(),
                                        homeworkDesc = (contents.content?.homework?.map { it.description?:"" } ?:"").toString(),
                                        homeworkName = homeworkFileName.toString(),
                                        isExternal = contents.content?.file_name?.contains("DPPs") == true
                                    )
                                }
                         //   binding.tvContentCount.text = "(${subjectContentList.size + (folderProgressContent?.size ?: 0)})"
                            val folderContentIs = folderProgressContent.filter { it.content?.file_type?.name  == "VIDEO" }.mapNotNull { it.content?.id }.toCollection(ArrayList())
                            val folderContentNes = folderProgressContent.filter { it.content?.file_type?.name  == "URL" }.mapNotNull { it.content?.file_name }.toCollection(ArrayList())
                            Log.e("getfoldersubject1",folderContentNes.toString())
                            binding.rvYTVideoListing.adapter = TopicContentAdapter(
                                subjectContentList.toMutableList(),
                                folderId,
                                binding.tvTopicType.text.toString(),
                                requireActivity(),
                                requireContext()
                            ) { topicContent, folderContentId, folderContentIds,folderContentNames, folderContentDescs,homework,homeworklinks, homeworkdes ->
                                when (topicContent.fileType) {

                                    "VIDEO" -> {
                                        //videoUrlApi(videourlViewModel, topicContent.id,topicContent.topicName,folderContentIds,folderContentNames,folderContentDescs)
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
                       //     binding.tvContentCount.text = "(0)"
                            binding.rvYTCourseVideoListing.adapter = null
                         //   binding.rvStudyMaterial.adapter = null
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
    private fun goToPlayerPage(context: Context, videoUrl: String, name: String) {
        val intent = Intent(context, YoutubeActivity::class.java).apply {
            putExtra("url", videoUrl)
        }
        context.startActivity(intent)
    }

    private fun videoUrlApi(viewModel: VideourlViewModel, folderContentId: String, name:String, folderContentIds: ArrayList<String>?, folderContentNames: ArrayList<String>?, folderContentDescs: ArrayList<String>?) {
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
                    putStringArrayList("folderContentDescs", folderContentDescs)
                    putStringArrayList("folderContentNames", folderContentNames)
                }
                findNavController().navigate(R.id.mediaFragment, bundle)

            } else {
                // Handle error or null URL
            }
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
                val minuteString = if (minutes > 0) "${minutes} min${if (minutes > 1) "s" else ""}" else ""
                val secondString = if (seconds > 0) "${seconds} sec" else ""

                listOf(hourString, minuteString, secondString).filter { it.isNotEmpty() }.joinToString(" ").trim()
            }
        }
    }
    private fun FileProgress(folderId: String, folderNames: String,folderCount: String){

        Log.e("FileProgresss $folderNames",folderId)
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
                            Log.e("studyMaterialProgress", subfolderDurationFolders.toString())

                            val gson = Gson()
                            val subFoldersJson = gson.toJson(subfolderDurationFolders)
                            val foldercontent =  gson.toJson(folderProgressContent)
                            Log.e("folderNamesss $name", subFoldersJson)

                            val bundle = Bundle().apply {
                                putString("subFolders", subFoldersJson)
                                putString("folder_Name", name)
                                putString("folder_Id", folderId)
                                putString("parent_content", foldercontent)
                            }
                            findNavController().navigate(R.id.SubjectContentFragment, bundle)
                        } else if (!folderProgressContent.isNullOrEmpty())
                        {
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
                    } else {
                        Log.e("studymatfile", "No content available")
                //        binding.tvContentCount.text = "(0)"
                      //  binding.rvSubjectContent.adapter = null
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
}