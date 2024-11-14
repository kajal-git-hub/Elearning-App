package xyz.penpencil.competishun.ui.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.student.competishun.curator.FindCourseFolderProgressQuery
import xyz.penpencil.competishun.data.model.TopicTypeModel
import xyz.penpencil.competishun.ui.adapter.TopicTypeAdapter
import xyz.penpencil.competishun.ui.viewmodel.CoursesViewModel
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.databinding.FragmentBottomsheetCourseTopicTypeBinding
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.utils.OnTopicTypeSelectedListener

@AndroidEntryPoint
class BottomsheetCourseTopicTypeFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomsheetCourseTopicTypeBinding
    private lateinit var topicTypeAdapter: TopicTypeAdapter
    private val coursesViewModel: CoursesViewModel by viewModels()
    private var listener: OnTopicTypeSelectedListener? = null
    val gson = Gson()
    private var selectedFolderName: String= ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomsheetCourseTopicTypeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        val subFolders = arguments?.getString("subFolders")?:""
        Log.e("subsdfd",subFolders)
        val folderCount = arguments?.getString("folder_Count")?:"0"
        selectedFolderName = arguments?.getString("FOLDER_NAME")?:""
        val converter = object : TypeToken<List<FindCourseFolderProgressQuery.SubfolderDuration>>() {}.type
        val subFoldersList: List<FindCourseFolderProgressQuery.SubfolderDuration> = gson.fromJson(subFolders, converter)

        Log.e("subFoldersListzz",subFoldersList.toString())
        // Create a list of TopicTypeModel using folderNames
        val topicTypeList = subFoldersList.mapIndexed { index, folder ->
            TopicTypeModel(id = folder.folder?.id?:"", title = folder.folder?.name?:"", count = folder.folder?.folder_count?:"0")
        }
        binding.tvTitleNumber.text = "(${subFoldersList.size})"


        Log.e("YUYUYUY", "onViewCreated: $selectedFolderName", )
        // Initialize the adapter and set it to the RecyclerView
        topicTypeAdapter = TopicTypeAdapter(topicTypeList, selectedFolderName) { selectedTopic ->
            listener?.onTopicTypeSelected(selectedTopic)
            dismiss()

        }
        binding.rvTopicTypes.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = topicTypeAdapter
        }
    }



    fun setOnTopicTypeSelectedListener(listener: OnTopicTypeSelectedListener) {
        this.listener = listener
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener = null
    }

}