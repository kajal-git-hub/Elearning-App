package xyz.penpencil.competishun.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.student.competishun.curator.FindCourseFolderProgressQuery
import xyz.penpencil.competishun.data.model.SubjectContentItem
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.data.model.TopicTypeModel
import xyz.penpencil.competishun.di.SharedVM
import xyz.penpencil.competishun.ui.adapter.SubjectContentAdapter
import xyz.penpencil.competishun.ui.adapter.TopicContentAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.CoursesViewModel
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.OnTopicTypeSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentSubjectContentBinding
import xyz.penpencil.competishun.di.Result
import xyz.penpencil.competishun.ui.main.PdfViewActivity

@AndroidEntryPoint
class SubjectContentFragment : DrawerVisibility() {

    private lateinit var binding: FragmentSubjectContentBinding
    private val coursesViewModel: CoursesViewModel by viewModels()
    private lateinit var helperFunctions: HelperFunctions
    private lateinit var subjectContentAdapter: SubjectContentAdapter
    private val videourlViewModel: VideourlViewModel by viewModels()
    private var VwatchedDuration: Int = 0
    val gson = Gson()
    private var isFirstTimeLoading = true
    private lateinit var sharedViewModel: SharedVM
    private var isExternal: Boolean = false
    var folderName = ""

    private var subfolder = -1

    private var folderProgress = -1

    private var folderProgressCont = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubjectContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIconSubjectContent.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        helperFunctions = HelperFunctions()

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        val subFolders = arguments?.getString("subFolders") ?: ""
         folderName = arguments?.getString("folder_Name") ?: ""
        val parentContent = arguments?.getString("parent_content") ?: ""
        val folderId =  arguments?.getString("folder_Id") ?: ""
        var folder_Count = arguments?.getString("folder_Count") ?: "0"
        isExternal = arguments?.getBoolean("isExternal", false) == true
        val subFolderList =
            object : TypeToken<List<FindCourseFolderProgressQuery.SubfolderDuration>>() {}.type
        val subFoldersList: List<FindCourseFolderProgressQuery.SubfolderDuration> =
            gson.fromJson(subFolders, subFolderList)
        Log.e("subdaaf", subFoldersList.toString())


        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedVM::class.java)
        sharedViewModel.watchedDuration.observe(viewLifecycleOwner, Observer { duration ->
            VwatchedDuration = duration
            Log.d("SubjectContentFragment", "Received duration: $duration")
            // Update UI or perform actions based on the received duration
        })

        Log.e("folderParent", parentContent.toString())

        if (parentContent.isNotEmpty()) {
            val subContentList =
                object : TypeToken<List<FindCourseFolderProgressQuery.FolderContent>>() {}.type
            val subContentsList: List<FindCourseFolderProgressQuery.FolderContent> =
                gson.fromJson(parentContent, subContentList)
            if (subContentsList != null) {
                val contents = subContentsList.forEach { it.content }
                newContent(subContentsList, folderId)
                //  folderProgress(subContentsList.forEach { it.content })
            }
        }
        binding.rvSubjectContent.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvTopicContent.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        if (subFoldersList[0].folder?.id != null) {
            var id = subFoldersList[0].folder?.id ?: ""
            binding.tvTopicType.text = subFoldersList[0].folder?.name
            folderProgress(id)
        }
        binding.clTopicType.setOnClickListener {
            val bundle = Bundle().apply {
                putString("subFolders", subFolders)
                putString("folder_Count", folder_Count)
                putString("FOLDER_NAME", binding.tvTopicType.text.toString())
            }

            val bottomSheet = BottomsheetCourseTopicTypeFragment().apply {
                arguments = bundle
            }

            bottomSheet.setOnTopicTypeSelectedListener(object : OnTopicTypeSelectedListener {
                override fun onTopicTypeSelected(selectedTopic: TopicTypeModel) {
                    binding.tvTopicType.text = selectedTopic.title
                    subfolder= -1
                    folderProgressCont= -1
                    folderProgress(selectedTopic.id)
                }
            })
            bottomSheet.show(childFragmentManager, "BottomsheetCourseTopicTypeFragment")


        }
        binding.mtCount.text = "${subFoldersList.size} Chapters"
        binding.tvSubjectName.text = folderName ?: ""
    }


    private fun folderProgress(folderId: String) {
        Log.e("allfolderProgress", folderId)
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
                    val folderProgressFolder = data.findCourseFolderProgress.folder
                    var folderProgressContent = data.findCourseFolderProgress.folderContents
                    val subfolderDurationFolders = data.findCourseFolderProgress.subfolderDurations
                    Log.e("subFolderdata", subfolderDurationFolders.toString())

                    subfolder = if (subfolderDurationFolders?.isEmpty() == true){
                        0
                    }else {
                        -1
                    }

                    folderProgressCont = if (folderProgressContent?.isEmpty() == true){
                        0
                    }else {
                        -1
                    }

                    if(subfolder==0 && folderProgressCont==0){
                        binding.clEmptySubject.visibility = View.VISIBLE
                        binding.rvSubjectContent.visibility = View.GONE
                        binding.rvsubjectTopicContent.visibility = View.GONE
                        binding.rvTopicContent.visibility = View.GONE
                    }else{
                        binding.clEmptySubject.visibility = View.GONE
                        binding.rvSubjectContent.visibility = View.VISIBLE
                        binding.rvsubjectTopicContent.visibility = View.VISIBLE
                        binding.rvTopicContent.visibility = View.VISIBLE
                    }

                    // Clear previous adapter to prevent issues
                    binding.rvSubjectContent.adapter = null
                    binding.rvTopicContent.adapter = null

                    when {
                        !subfolderDurationFolders.isNullOrEmpty() && !folderProgressContent.isNullOrEmpty() ->
                            {
                                Log.e("insideboth",subfolderDurationFolders.toString())
                                binding.tvContentCount.text = "(${subfolderDurationFolders.size})"
                             //   binding.tvContentCount.text = "(${folderCounts.joinToString()})"
                            val topicContentList =
                                folderProgressContent.mapIndexed { index, files ->
                                    Log.e("folderContentLog", files.content?.file_url.toString())
                                    Log.e("homeworkLog", files.content?.homework.toString())
                                    val time = helperFunctions.formatCoursesDateTime(files.content?.scheduled_time.toString())
                                    Log.e("foldertimes", time)

                                    // Extract homework URL and filename if they exist
                                    val homeworkUrl = files.content?.homework?.map { it.file_url } ?:""
                                    val homeworkFileName = files.content?.homework?.map { it.file_name } ?: ""
                                    Log.e("foldersfd",folderName)
                                    val icon = when (folderName) {
                                        "Chemistry" -> R.drawable.chemistory
                                        "Physics" -> R.drawable.pdf_bg
                                        "Mathematics" -> R.drawable.message
                                        else -> R.drawable.download_person // Set a default icon if folder name doesn't match
                                    }

                                    TopicContentModel(
                                        subjectIcon = if (files.content?.file_type?.name == "PDF") R.drawable.content_bg else R.drawable.group_1707478994,
                                        id = files.content?.id ?: "",
                                        playIcon = if (files.content?.file_type?.name == "VIDEO") R.drawable.video_bg else 0,
                                        lecture = if (files.content?.file_type?.name == "VIDEO") "Lecture" else "Study Material",
                                        lecturerName = if(files.content?.file_type?.name == "VIDEO") formatTimeDuration(
                                            files.content.video_duration ?: 0
                                        ) else "",
                                        topicName = files.content?.file_name ?: "",
                                        topicDescription = files.content?.description.toString(),
                                        progress = 1,
                                        videoDuration = files.content?.video_duration ?: 0,
                                        url = files.content?.file_url.toString(),
                                        fileType = files.content?.file_type?.name ?: "",
                                        lockTime = time,
                                        // Assign homework name and URL here
                                        homeworkName = homeworkFileName.toString(),
                                        homeworkDesc = files.content?.homework?.map { it.description }.toString() ?: "",
                                        homeworkUrl = homeworkUrl.toString() , // Add this field in your TopicContentModel if it doesn't exist
                                        isExternal = isExternal
                                    )
                                }

                                val folderContentIdd = folderProgressContent.filter { it.content?.file_type?.name  == "VIDEO" }.mapNotNull { it.content?.id }.toCollection(ArrayList())
                                val folderContentNs = folderProgressContent.filter { it.content?.file_type?.name  == "VIDEO" }.mapNotNull { it.content?.file_name }.toCollection(ArrayList())
                                Log.e("getfoldersubject2",folderContentNs.toString())
                            binding.rvTopicContent.adapter = TopicContentAdapter(
                                topicContentList.toMutableList(),
                                folderId,
                                folderName,
                                requireActivity(),
                                requireContext()
                            ) { topicContent, folderContentId, folderContentIds ,folderContentNames, folderContentDesc, folderContenthomework, folderContenthomeworkLink,folderContenthomeworkDesc ->
                                when (topicContent.fileType) {
                                    "VIDEO" -> {
                                        videoUrlApi(videourlViewModel, topicContent.id,topicContent.topicName,folderContentIds,folderContentNames,folderContentDesc,folderContenthomework,folderContenthomeworkLink,folderContenthomeworkDesc)
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
                                val time = helperFunctions.formatCoursesDateTime(date)
                                SubjectContentItem(
                                    id = id,
                                    chapterNumber = index + 1,
                                    topicName = folders.name,
                                    topicDescription = folders.folder_count?:"0",
                                    locktime = time,
                                    progressPer = subfolderDurationFolders[index].completionPercentage.toInt(),
                                    isExternal = isExternal
                                )
                            }

                            binding.rvSubjectContent.adapter =
                                SubjectContentAdapter(subjectContentList) { selectedItem ->
                                    Log.e("enewfSlectt",selectedItem.topicName)
                                    //binding.tvTopicType.text = selectedItem.topicName
                                    videoProgress(
                                        selectedItem.id,
                                        currentDuration = VwatchedDuration
                                    )
                                    FileProgress(selectedItem.id,selectedItem.topicName,"")


                                }

//                                binding.rvSubjectContent.adapter = null
//                                binding.rvTopicContent.adapter = null
                        }

                        !subfolderDurationFolders.isNullOrEmpty() -> {
                            Log.e("kajal2", subfolderDurationFolders.toString())
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
                                val time = helperFunctions.formatCoursesDateTime(date)
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
                                    Log.e("enewfSlect",selectedItem.topicName)
                                    videoProgress(
                                        selectedItem.id,
                                        currentDuration = VwatchedDuration
                                    )
                                     FileProgress(selectedItem.id,selectedItem.topicName,"")


                                }

                        }

                        !folderProgressContent.isNullOrEmpty() -> {
                            Log.e("kaja1", folderProgressContent.toString())
                            binding.tvContentCount.text = "(${folderProgressContent.size})"

                            val subjectContentList =
                                folderProgressContent.mapIndexed { index, contents ->
                                    Log.e("folderContentLog", contents.content?.file_url.toString())

                                    val homeworkUrl = contents.content?.homework?.map { it.file_url } ?:""
                                    val homeworkFileName = contents.content?.homework?.map { it.file_name } ?: ""

                                    Log.e("foldersfd",folderName)
                                    val time = helperFunctions.formatCoursesDateTime(contents.content?.scheduled_time.toString())
                                    Log.e("foldertime", time)
                                    val icon = when (folderName) {
                                        "Chemistry" -> R.drawable.chemistory
                                        "Physics" -> R.drawable.pdf_bg
                                        "Mathematics" -> R.drawable.message
                                        else -> R.drawable.download_person // Set a default icon if folder name doesn't match
                                    }
                                    TopicContentModel(
                                        subjectIcon = if (contents.content?.file_type?.name == "PDF") R.drawable.content_bg else R.drawable.group_1707478994,
                                        id = contents.content?.id ?: "",
                                        playIcon = if (contents.content?.file_type?.name == "VIDEO") R.drawable.video_bg else 0,
                                        lecture = if (contents.content?.file_type?.name == "VIDEO") "Lecture" else "Study Material",
                                        lecturerName = if(contents.content?.file_type?.name == "VIDEO") formatTimeDuration(
                                            contents.content.video_duration ?: 0
                                        ) else "" ,
                                        topicName = contents.content?.file_name ?: "",
                                        topicDescription = contents.content?.description.toString(),
                                        progress = 1,
                                        videoDuration = contents.content?.video_duration
                                            ?: 0,
                                        url = contents.content?.file_url.toString(),
                                        fileType = contents.content?.file_type?.name ?: "",
                                        lockTime =  time,
                                        homeworkUrl = homeworkUrl.toString(),
                                        homeworkDesc = contents.content?.homework?.map { it.description }.toString() ?: "",
                                        homeworkName = homeworkFileName.toString(),
                                        isExternal = isExternal
                                    )
                                }
                            val folderContentIds = folderProgressContent.filter { it.content?.file_type?.name  == "VIDEO" }.mapNotNull { it.content?.id }.toCollection(ArrayList())
                            binding.rvTopicContent.adapter = TopicContentAdapter(
                                subjectContentList.toMutableList(),
                                folderId,
                                folderName,
                                requireActivity(),
                                requireContext()
                            ) { topicContent, folderContentId, folderContentIds, folderContentNames, folderContentDesc,folderContenthomework,folderContenthomeworkurl,folderContenthomeworkDesc ->
                                when (topicContent.fileType) {

                                    "VIDEO" -> {
                                        videoUrlApi(videourlViewModel, topicContent.id,topicContent.topicName,folderContentIds,folderContentNames,folderContentDesc,folderContenthomework,folderContenthomeworkurl,folderContenthomeworkDesc)
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
                            binding.rvSubjectContent.adapter = null
                        }
                    }
                    folderProgressContent = emptyList()
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

    private fun subfolderProgress(folderId: String) {
        Log.e("allfoldernot", folderId)
        val free = arguments?.getBoolean("free")

        if (folderId.isNotEmpty()) {
            // Trigger the API call
            coursesViewModel.findCourseFolderProgress(folderId)
        }

        coursesViewModel.courseFolderProgress.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    // Handle the loading state
                    Log.d("folderProgressub", "Loading...")
                    // Optionally show a loading indicator here
                }

                is Result.Success -> {
                    val data = result.data
                    Log.e("folderProgressub", data.toString())
                    Log.e("findCoursgress", data.findCourseFolderProgress.folder.toString())
                    val folderProgressFolder = data.findCourseFolderProgress.folder
                    var folderProgressContent = data.findCourseFolderProgress.folderContents

//                    if (folderProgressContent?.isEmpty() == true){
//                        folderProgress = 0
//                    }

                    if (!folderProgressContent.isNullOrEmpty() ){
                            Log.e("folderContentsub" +
                                    "", folderProgressContent.toString())
                         //   binding.tvContentCount.text = "(${folderProgressContent.size})"

                            var subjectContentList =
                                folderProgressContent.mapIndexed { index, contents ->
                                    Log.e("folderContentLog", contents.content?.file_url.toString())

                                    val homeworkUrl = contents.content?.homework?.map { it.file_url } ?:""
                                    val homeworkFileName = contents.content?.homework?.map { it.file_name } ?: ""

                                    Log.e("foldersfd",folderName)
                                    val time = helperFunctions.formatCoursesDateTime(contents.content?.scheduled_time.toString())
                                    Log.e("foldertime", time)
                                    val icon = when (folderName) {
                                        "Chemistry" -> R.drawable.chemistory
                                        "Physics" -> R.drawable.pdf_bg
                                        "Mathematics" -> R.drawable.message
                                        else -> R.drawable.download_person // Set a default icon if folder name doesn't match
                                    }
                                    TopicContentModel(
                                        subjectIcon = if (contents.content?.file_type?.name == "PDF") R.drawable.content_bg else R.drawable.group_1707478994,
                                        id = contents.content?.id ?: "",
                                        playIcon = if (contents.content?.file_type?.name == "VIDEO") R.drawable.video_bg else 0,
                                        lecture = if (contents.content?.file_type?.name == "VIDEO") "Lecture" else "Study Material",
                                        lecturerName = if(contents.content?.file_type?.name == "VIDEO") formatTimeDuration(
                                            contents.content.video_duration ?: 0
                                        ) else "" ,
                                        topicName = contents.content?.file_name ?: "",
                                        topicDescription = contents.content?.description.toString(),
                                        progress = 1,
                                        videoDuration = contents.content?.video_duration
                                            ?: 0,
                                        url = contents.content?.file_url.toString(),
                                        fileType = contents.content?.file_type?.name ?: "",
                                        lockTime =  time,
                                        homeworkUrl = homeworkUrl.toString(),
                                        homeworkDesc = contents.content?.homework?.map { it.description }.toString() ?: "",
                                        homeworkName = homeworkFileName.toString(),
                                        isExternal = isExternal
                                    )
                                }
                            val folderConteIds = folderProgressContent.filter { it.content?.file_type?.name  == "VIDEO" }.mapNotNull { it.content?.id }.toCollection(ArrayList())
                            val folderContentNs = folderProgressContent.filter { it.content?.file_type?.name  == "VIDEO" }.mapNotNull { it.content?.file_name }.toCollection(ArrayList())
                            Log.e("getfoldersubject1",folderContentNs.toString())
                            binding.rvsubjectTopicContent.adapter = TopicContentAdapter(
                                subjectContentList.toMutableList(),
                                folderId,
                                folderName,
                                requireActivity(),
                                requireContext()
                            ) { topicContent, folderContentId, folderContentIds, folderContentNames, folderContentDesc, folderContenthomework,folderContenthomeworkLink,folderContenthomeworkDesc ->
                                when (topicContent.fileType) {

                                    "VIDEO" -> {
                                        videoUrlApi(videourlViewModel, topicContent.id,topicContent.topicName,folderContentIds,folderContentNames, folderContentDesc,folderContenthomework,folderContenthomeworkLink,folderContenthomeworkDesc)
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
                    folderProgressContent = emptyList()

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

    fun newContent(folderContents: List<FindCourseFolderProgressQuery.FolderContent>,folderId:String) {
        val topicContents = folderContents?.map { content ->
            val date = content.content?.scheduled_time.toString()
            var time = ""
            var studyMaterial = arguments?.getString("studyMaterial")

            if (studyMaterial.isNullOrEmpty())
                time = helperFunctions.formatCoursesDateTime(date) else  time = helperFunctions.formatCoursesDateTime("2024-10-11T17:27:00.000Z")

            val homeworkUrl = content.content?.homework?.map { it.file_url?:"" } ?:""
            val homeworkFileName = content.content?.homework?.map { it.file_name?:"" } ?: ""
            Log.d("homeworkUrl",homeworkUrl.toString())
            Log.d("homeworkFileName",homeworkFileName.toString())
            Log.e("foldersfd",folderName)
            val icon = when (folderName) {
                "Chemistry" -> R.drawable.chemistory
                "Physics" -> R.drawable.pdf_bg
                "Mathematics" -> R.drawable.message
                else -> R.drawable.download_person // Set a default icon if folder name doesn't match
            }

            TopicContentModel(
                subjectIcon = if (content.content?.file_type?.name == "PDF") R.drawable.content_bg else R.drawable.group_1707478994,
                id = content.content?.id ?: "",
                playIcon = if (content.content?.file_type?.name == "VIDEO") R.drawable.video_bg else 0,
                lecture = if (content.content?.file_type?.name == "VIDEO") "Lecture" else "Study Material",
                lecturerName = "",
                topicName = content.content?.file_name ?: "",
                topicDescription = content.content?.description ?:"",
                progress = 1,
                videoDuration = content.content?.video_duration ?: 0,
                url = content.content?.file_url.toString(),
                fileType = content.content?.file_type?.name ?: "",
                lockTime = time,
                homeworkUrl = homeworkUrl.toString(),
                homeworkDesc = content.content?.homework?.map { it.description }.toString() ?: "",
                homeworkName = homeworkFileName.toString(),
                isExternal = isExternal
            )
        } ?: emptyList()

        val ContentIds = folderContents.filter { it.content?.file_type?.name  == "VIDEO" }.mapNotNull { it.content?.id }?.toCollection(ArrayList())
        val folderContentNas = folderContents.filter { it.content?.file_type?.name  == "VIDEO" }.mapNotNull { it.content?.file_name }?.toCollection(ArrayList())

        val adapter = TopicContentAdapter(topicContents.toMutableList(), folderId,folderName,requireActivity(),requireContext()) { topicContent, folderContentId , folderContentIds,folderContentNames, folderContentDesc, folderContenthomework, folderContenthomeworkLink,folderContenthomeworkDesc->
            when (topicContent.fileType) {
                "VIDEO" -> videoUrlApi(videourlViewModel, topicContent.id,topicContent.topicName,folderContentIds,folderContentNames, folderContentDesc, folderContenthomework, folderContenthomeworkLink,folderContenthomeworkDesc)
                "PDF" -> {
                    val intent = Intent(context, PdfViewActivity::class.java).apply {
                        putExtra("PDF_URL", topicContent.url)
                        putExtra("PDF_TITLE",topicContent.topicName)
                    }
                    context?.startActivity(intent)
                }
                "FOLDER" -> "Folders"
                else -> Log.d("TopicContentAdapter", "File type is not VIDEO: ${topicContent.fileType}")
            }
        }

        binding.rvsubjectTopicContent.adapter = adapter
        binding.rvsubjectTopicContent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvTopicContent.adapter = null

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

    private fun videoUrlApi(viewModel: VideourlViewModel, folderContentId: String, name:String, folderContentIds: ArrayList<String>?, folderContentNames: ArrayList<String>?,folderContentDescs: ArrayList<String>?,homeworkNames:ArrayList<String>?,homeworkLinks:ArrayList<String>?,homeworkDescs:ArrayList<String>?) {
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
                    putStringArrayList("folderContentDescs", folderContentDescs)
                    putStringArrayList("homeworkNames", homeworkNames)
                    putStringArrayList("homeworkLinks", homeworkLinks)
                    putStringArrayList("homeworkDescs", homeworkDescs)
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
                            Log.e("subfolderDurationsz", subfolderDurationFolders.toString())

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
                                putString("folderNames", folderNames)
                                putString("folderName", folderName)

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
}
