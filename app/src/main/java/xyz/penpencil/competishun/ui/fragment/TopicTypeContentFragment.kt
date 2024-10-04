package xyz.penpencil.competishun.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.student.competishun.curator.FindCourseFolderProgressQuery
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.ui.adapter.TopicContentAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.CoursesViewModel
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import xyz.penpencil.competishun.utils.HelperFunctions
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentTopicTypeContentBinding
import xyz.penpencil.competishun.di.Result
import xyz.penpencil.competishun.ui.main.PdfViewerActivity

@AndroidEntryPoint
class TopicTypeContentFragment : Fragment() {

    private lateinit var binding: FragmentTopicTypeContentBinding
    private val coursesViewModel: CoursesViewModel by viewModels()
    private val videourlViewModel: VideourlViewModel by viewModels()

    private lateinit var helperFunctions: HelperFunctions
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
        binding.backIcon.setOnClickListener {

                requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        val gson = Gson()
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
        val folderId = arguments?.getString("folder_Id")?:""
        var folder_Name = arguments?.getString("folderName")
        var folderContents = arguments?.getString("folderContents")?:"0"
        val subContentList = object : TypeToken< List<FindCourseFolderProgressQuery. FolderContent>>() {}.type
        val subContentsList:  List<FindCourseFolderProgressQuery. FolderContent> = gson.fromJson(folderContents, subContentList)
        binding.tvTopicTypeName.text = folder_Name?:""
        if (subContentsList != null) {
            val contents = subContentsList.forEach { it.content }
            newContent(subContentsList,folderId)
          //  folderProgress(subContentsList.forEach { it.content })
        }


        helperFunctions = HelperFunctions()
        binding.tvTopicTypeName.text = folder_Name?:""

        binding.tvTopicContentCount.text = "0${subContentsList.size}"

    }

    fun newContent(folderContents: List<FindCourseFolderProgressQuery.FolderContent>,folderId:String)
    {
        val topicContents = folderContents?.map { content ->
            val date = content.content?.scheduled_time.toString()?:""
            val time = helperFunctions.formatCourseDateTime(date)
            TopicContentModel(
                subjectIcon = if (content.content?.file_type?.name == "PDF") R.drawable.content_bg else R.drawable.group_1707478994,
                id = content.content?.id ?: "",
                playIcon = if (content.content?.file_type?.name == "VIDEO") R.drawable.video_bg else 0,
                lecture = if (content.content?.file_type?.name == "VIDEO") "Lecture" else "Study Material",
                lecturerName = "Ashok",
                topicName = content.content?.file_name ?: "",
                topicDescription = content.content?.description.toString(),
                progress = 1,
                videoDuration = content.content?.video_duration ?: 0,
                url = content.content?.file_url.toString(),
                fileType = content.content?.file_type?.name ?: "",
                lockTime = time
            )
        } ?: emptyList()
        val adapter = TopicContentAdapter(topicContents, folderId,requireActivity()) { topicContent, folderContentId ->
            when (topicContent.fileType) {
                "VIDEO" -> videoUrlApi(videourlViewModel, topicContent.id,topicContent.topicName)
                "PDF" -> {
                    val intent = Intent(context, PdfViewerActivity::class.java).apply {
                        putExtra("PDF_URL", topicContent.url)
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
                    val folderContents = data.findCourseFolderProgress.folderContents
                    Log.d("folderContentsProgress", folderContents.toString())
                    val topicContents = folderContents?.map { content ->
                        val time = helperFunctions.formatCourseDateTime(content.content?.scheduled_time.toString())
                        TopicContentModel(
                            subjectIcon = R.drawable.group_1707478994, // Replace with dynamic icon if needed
                            id = content.content?.id.orEmpty(),
                            playIcon = R.drawable.video_bg, // Replace with dynamic icon if needed
                            lecture = "Lecture", // Replace with dynamic data if available
                            lecturerName = content.content?.file_name.orEmpty(),
                            topicName = content.content?.file_name.orEmpty(),
                            topicDescription = content.content?.file_name.orEmpty(),
                            progress = content.videoCompletionPercentage?.toInt() ?: 0,
                            url = content.content?.file_url.orEmpty(),
                            videoDuration = content.content?.video_duration ?: 0,
                            fileType = content.content?.file_type?.name.orEmpty(),
                            lockTime = time
                        )
                    } ?: emptyList()

                    val adapter = TopicContentAdapter(topicContents, folderId,requireActivity()) { topicContent, folderContentId ->
                        when (topicContent.fileType) {
                            "VIDEO" -> videoUrlApi(videourlViewModel, topicContent.id,topicContent.topicName)
                            "PDF" -> {
                                val intent = Intent(context, PdfViewerActivity::class.java).apply {
                                    putExtra("PDF_URL", topicContent.url)
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
    fun videoUrlApi(viewModel: VideourlViewModel, folderContentId:String, name:String){

        viewModel.fetchVideoStreamUrl(folderContentId, "360p")

        viewModel.videoStreamUrl.observe(viewLifecycleOwner, { signedUrl ->
            Log.d("Videourl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                val bundle = Bundle().apply {
                    putString("url", signedUrl)
                    putString("url_name", name)
                }
                findNavController().navigate(R.id.mediaFragment,bundle)

            } else {
                // Handle error or null URL
            }
        })
    }


}
