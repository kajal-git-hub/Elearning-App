package xyz.penpencil.competishun.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.student.competishun.curator.FindCourseFolderProgressQuery
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.databinding.FragmentTopicTypeContentBinding
import xyz.penpencil.competishun.di.Result
import xyz.penpencil.competishun.ui.adapter.TopicContentAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.main.PdfViewActivity
import xyz.penpencil.competishun.ui.viewmodel.CoursesViewModel
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import xyz.penpencil.competishun.utils.HelperFunctions


@AndroidEntryPoint
class TopicTypeContentFragment : DrawerVisibility() {

    private lateinit var binding: FragmentTopicTypeContentBinding
    private val coursesViewModel: CoursesViewModel by viewModels()
    private val videourlViewModel: VideourlViewModel by viewModels()

    private lateinit var helperFunctions: HelperFunctions
    private var isExternal = false

    private var pageNumber = 0
    private var pageSize = 10

    private var subContentsList:  MutableList<FindCourseFolderProgressQuery. FolderContent> = mutableListOf()

    var adapter : TopicContentAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopicTypeContentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        helperFunctions = HelperFunctions()
        binding.tvTopicTypeName.setOnClickListener {
            it.findNavController().popBackStack()
        }
        val gson = Gson()
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
        val folderId = arguments?.getString("folder_Id")?:""
        val folder_Name = arguments?.getString("folderName")
        val folderContents = arguments?.getString("folderContents")?:"0"
         isExternal = arguments?.getBoolean("isExternal", false) == true
        val subContentList = object : TypeToken< List<FindCourseFolderProgressQuery. FolderContent>>() {}.type
        subContentsList = gson.fromJson(folderContents, subContentList)
        binding.tvTopicTypeName.text = folder_Name?:""

        if (subContentsList.isNotEmpty()) {
//            binding.progress.visibility = View.VISIBLE
            binding.clEmptyContent.visibility = View.GONE
            pageSize = if (subContentsList.size<=10) subContentsList.size else 10
            val list  = subContentsList.subList(pageNumber, pageSize)
            newContent(list, folderId, true)
        }else {
//            binding.progress.visibility = View.GONE
            binding.clEmptyContent.visibility = View.VISIBLE
        }


        helperFunctions = HelperFunctions()
        binding.tvTopicTypeName.text = folder_Name?:""

        binding.tvTopicContentCount.text = "0${subContentsList.size}"

        view.setFocusableInTouchMode(true)
        view.requestFocus()
        view.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    v?.findNavController()?.popBackStack()
                    return true
                }
                return false
            }
        })

        binding.rvTopicContent.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                val visibleItemCount = layoutManager?.childCount ?: 0
                val totalItemCount = layoutManager?.itemCount ?: 0
                val firstVisibleItemPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                    firstVisibleItemPosition >= 0
                ) {
                    pageSize = firstVisibleItemPosition
                    newContent(subContentsList, folderId, true)
                }
            }
        })

    }
    private fun newContent(folderContents: List<FindCourseFolderProgressQuery.FolderContent>, folderId: String, isFirstTime: Boolean = false) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val topicContents = async(Dispatchers.Default) {
                    folderContents.map { content ->
                        val date = content.content?.scheduled_time.toString()
                        val studyMaterial = arguments?.getString("studyMaterial")
                        val time = if (studyMaterial.isNullOrEmpty()) {
                            helperFunctions.formatCoursesDateTime(date)
                        } else {
                            helperFunctions.formatCoursesDateTime("2024-10-11T17:27:00.000Z")
                        }

                        val homeworkUrl = content.content?.homework?.map { it.file_url ?: "" } ?: ""
                        val homeworkFileName = content.content?.homework?.map { it.file_name ?: "" } ?: ""

                        TopicContentModel(
                            subjectIcon = if (content.content?.file_type?.name == "PDF") R.drawable.content_bg else R.drawable.group_1707478994,
                            id = content.content?.id ?: "",
                            playIcon = if (content.content?.file_type?.name == "VIDEO") R.drawable.video_bg else 0,
                            lecture = if (content.content?.file_type?.name == "VIDEO") "Lecture" else "Study Material",
                            lecturerName = "",
                            topicName = content.content?.file_name ?: "",
                            topicDescription = content.content?.description ?: "",
                            progress = 1,
                            videoDuration = content.content?.video_duration ?: 0,
                            url = content.content?.file_url.toString(),
                            fileType = content.content?.file_type?.name ?: "",
                            lockTime = time,
                            homeworkDesc = content.content?.homework?.map { it.description }.toString() ?: "",
                            homeworkUrl = homeworkUrl.toString(),
                            homeworkName = homeworkFileName.toString(),
                            isExternal = isExternal
                        )
                    }
                }.await()

                if (isFirstTime){
                    if (topicContents.isNotEmpty()) {
                        binding.rvTopicContent.visibility = View.VISIBLE
                        binding.clEmptyContent.visibility = View.GONE
                    } else {
                        binding.rvTopicContent.visibility = View.GONE
                        binding.clEmptyContent.visibility = View.VISIBLE
                    }
//                    binding.progress.visibility = View.GONE

                    // Set up the adapter on the main thread
                    Log.e("UUIUIIUIU", "newContent: "+topicContents.size)
                    adapter = TopicContentAdapter(topicContents.toMutableList(), folderId, requireActivity(), requireContext()) { topicContent, folderContentId, folderContentIds, folderContentNames, folderContentDesc, folderContenthomework, folderContenthomeworkLink, folderContenthomeworkDesc ->
                        when (topicContent.fileType) {
                            "VIDEO" -> videoUrlApi(videourlViewModel, topicContent.id, topicContent.topicName, folderContentIds, folderContentNames, folderContentDesc, folderContenthomework, folderContenthomeworkLink, folderContenthomeworkDesc)
                            "PDF" -> {
                                val intent = Intent(context, PdfViewActivity::class.java).apply {
                                    putExtra("PDF_URL", topicContent.url)
                                    putExtra("PDF_TITLE", topicContent.topicName)
                                }
                                context?.startActivity(intent)
                            }
                            "FOLDER" -> "Folders"
                            else -> Log.d("TopicContentAdapter", "File type is not VIDEO: ${topicContent.fileType}")
                        }
                    }

                    binding.rvTopicContent.adapter = adapter
                    binding.rvTopicContent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }else {
                    adapter?.updateData(topicContents.toMutableList())
                    binding.rvTopicContent.scrollToPosition(pageSize)
//                    binding.recyclerProgress.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.e("newContent", "Error loading content", e)
            }

            if (isFirstTime){
                newContent(subContentsList, folderId, false)
            }
        }
    }


    /*
        private fun newContent(folderContents: List<FindCourseFolderProgressQuery.FolderContent>, folderId:String) {

            val topicContents = folderContents?.map { content ->
                val date = content.content?.scheduled_time.toString()
                var time = ""
                var studyMaterial = arguments?.getString("studyMaterial")

                time = if (studyMaterial.isNullOrEmpty())
                    helperFunctions.formatCoursesDateTime(date) else helperFunctions.formatCoursesDateTime("2024-10-11T17:27:00.000Z")

                val homeworkUrl = content.content?.homework?.map { it.file_url?:"" } ?:""
                val homeworkFileName = content.content?.homework?.map { it.file_name?:"" } ?: ""


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
                    homeworkDesc = content.content?.homework?.map { it.description }.toString() ?: "",
                    homeworkUrl = homeworkUrl.toString(),
                    homeworkName = homeworkFileName.toString(),
                    isExternal = isExternal
                )
            } ?: emptyList()

    */
/*        val folderContentis = folderContents.filter { it.content?.file_type?.name  == "VIDEO" }.mapNotNull { it.content?.id }?.toCollection(ArrayList())
        val folderContentNs = folderContents.filter { it.content?.file_type?.name  == "VIDEO" }.mapNotNull { it.content?.file_name }?.toCollection(ArrayList())*//*


        val adapter = TopicContentAdapter(topicContents, folderId,requireActivity(),requireContext()) { topicContent, folderContentId, folderContentIds,folderContentNames, folderContentDesc, folderContenthomework, folderContenthomeworkLink,folderContenthomeworkDesc->
            when (topicContent.fileType) {
                "VIDEO" -> videoUrlApi(videourlViewModel, topicContent.id,topicContent.topicName,folderContentIds,folderContentNames,folderContentDesc,folderContenthomework, folderContenthomeworkLink,folderContenthomeworkDesc)
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

        binding.rvTopicContent.adapter = adapter
        binding.rvTopicContent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }
*/

    fun folderProgress(folderId:String){
        if (folderId != null) {
            coursesViewModel.findCourseFolderProgress(folderId)
        }
        coursesViewModel.courseFolderProgress.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    // Handle the loading state
                    Log.d("folderProgress", result.toString())
                    // Optionally show a loading indicator here
                }
                is Result.Success -> {
                    val data = result.data
                    Log.e("GetFolderdatazz", data.findCourseFolderProgress.folder.toString())
                    if(data.findCourseFolderProgress.folderContents?.isNotEmpty() == true){
                        binding.rvTopicContent.visibility = View.VISIBLE
                        binding.clEmptyContent.visibility = View.GONE
                    }
                    val folderContents = data.findCourseFolderProgress.folderContents
                    Log.d("folderContentsProgress", folderContents.toString())

                    val topicContents = folderContents?.map { content ->

                        val homeworkUrl = content.content?.homework?.map { it.file_url?:"" } ?:""
                        val homeworkFileName = content.content?.homework?.map { it.file_name?:"" } ?: ""

                        Log.d("homeworkUrl",homeworkUrl.toString())

                        var time = ""
                        var studyMaterial = arguments?.getString("studyMaterial")

                        if (studyMaterial.isNullOrEmpty())
                            time = helperFunctions.formatCoursesDateTime(content.content?.scheduled_time.toString()) else  time = helperFunctions.formatCoursesDateTime("2024-10-11T17:27:00.000Z")
                        Log.e("scheduletimes",time.toString())

                        TopicContentModel(
                            subjectIcon = R.drawable.group_1707478994, // Replace with dynamic icon if needed
                            id = content.content?.id.orEmpty(),
                            playIcon = R.drawable.video_bg, // Replace with dynamic icon if needed
                            lecture = "Lecture", // Replace with dynamic data if available
                            lecturerName = "",
                            topicName = content.content?.file_name.orEmpty(),
                            topicDescription = content.content?.file_name.orEmpty(),
                            progress = content.videoCompletionPercentage?.toInt() ?: 0,
                            url = content.content?.file_url.orEmpty(),
                            videoDuration = content.content?.video_duration ?: 0,
                            fileType = content.content?.file_type?.name.orEmpty(),
                            lockTime = time,
                            homeworkDesc = content.content?.homework?.map { it.description }.toString() ?: "",
                            homeworkUrl = homeworkUrl.toString(),
                            homeworkName = homeworkFileName.toString(),
                            isExternal = isExternal
                        )
                    } ?: emptyList()
                    val folderContentIs = folderContents?.filter { it.content?.file_type?.name  == "VIDEO" }?.mapNotNull { it.content?.id }?.toCollection(ArrayList())
                    val folderContentNs = folderContents?.filter { it.content?.file_type?.name  == "VIDEO" }?.mapNotNull { it.content?.file_name }?.toCollection(ArrayList())
                    val adapter = TopicContentAdapter(topicContents.toMutableList(), folderId,requireActivity(),requireContext()) { topicContent, folderContentId, folderContentIds,folderContentNames, folderContentDescs,folderContenthomework, folderContenthomeworkLink,folderContenthomeworkDesc ->
                        when (topicContent.fileType) {
                            "VIDEO" -> videoUrlApi(videourlViewModel, topicContent.id,topicContent.topicName,folderContentIds,folderContentNames, folderContentDescs,folderContenthomework, folderContenthomeworkLink,folderContenthomeworkDesc)
                            "PDF" -> {
                                val intent = Intent(context, PdfViewActivity::class.java).apply {
                                    putExtra("PDF_URL", topicContent.url)
                                    putExtra("PDF_TITLE",topicContent.topicName)
                                }
                                context?.startActivity(intent)
                            }
                            else -> Log.d("TopicContentAdapter", "File type is not VIDEO: ${topicContent.fileType}")
                        }
                    }

                    binding.rvTopicContent.adapter = adapter
                    binding.rvTopicContent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                    val folderProgressFolder = data.findCourseFolderProgress.folder
                    // Additional processing with folderProgressFolder if needed

                }
                is Result.Failure -> {
                    // Handle the error
                    Log.e("AllDemoResourcesFree", result.exception.message.toString())
                    // Optionally show an error message or indicator
                }
            }
        }

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



}
