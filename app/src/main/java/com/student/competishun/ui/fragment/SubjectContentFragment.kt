package com.student.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.R
import com.student.competishun.curator.FindCourseFolderProgressQuery
import com.student.competishun.curator.type.FileType
import com.student.competishun.data.model.FreeDemoItem
import com.student.competishun.data.model.SubjectContentItem
import com.student.competishun.databinding.FragmentSubjectContentBinding
import com.student.competishun.ui.adapter.ExploreCourseAdapter
import com.student.competishun.ui.adapter.FreeDemoAdapter
import com.student.competishun.ui.adapter.SubjectContentAdapter
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.viewmodel.CoursesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubjectContentFragment : Fragment() {

    private var _binding: FragmentSubjectContentBinding? = null
    private val binding get() = _binding!!
    private val coursesViewModel: CoursesViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubjectContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

       // val folderId = arguments?.getString("folderId")

            folderProgress("81c5cc01-666c-4a97-a4b5-b153b1d9380e")

        binding.backIconSubjectContent.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        val subjectContentList = listOf(
            SubjectContentItem(1, "Trigonometric ratios", "08 Learning Material"),
            SubjectContentItem(2, "Pythagorean theorem", "05 Learning Material"),
            SubjectContentItem(3, "Vectors and scalars", "07 Learning Material"),
            SubjectContentItem(4, "Algebraic expressions", "10 Learning Material"),
            SubjectContentItem(5, "Calculus basics", "12 Learning Material"),
            SubjectContentItem(6, "Linear equations", "09 Learning Material"),
            SubjectContentItem(7, "Probability", "06 Learning Material"),
            SubjectContentItem(8, "Statistics", "08 Learning Material"),
            SubjectContentItem(8, "Statistics", "08 Learning Material"),
            SubjectContentItem(8, "Statistics", "08 Learning Material"),
            SubjectContentItem(8, "Statistics", "08 Learning Material"),
            SubjectContentItem(8, "Statistics", "08 Learning Material"),
        )

        binding.rvSubjectContent.adapter = SubjectContentAdapter(subjectContentList){courseitem->
            findNavController().navigate(R.id.action_subjectContentFragment_to_TopicTYPEContentFragment)
        }
        binding.rvSubjectContent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.downChooseTopic.setOnClickListener {
            val bottomSheet = BottomsheetCourseTopicTypeFragment()
            bottomSheet.show(childFragmentManager, "BottomsheetCourseTopicTypeFragment")
        }
    }

    fun folderProgress(folderId:String){
        if (folderId != null) {
            coursesViewModel.findCourseFolderProgress(folderId)
        }
        coursesViewModel.courseFolderProgress.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                Log.e("GetFolderdata", data.findCourseFolderProgress.folder.toString())
                val folderProgressFolder = data.findCourseFolderProgress.folder
                val folderProgressContent = data.findCourseFolderProgress.folderContents
                 binding.tvSubjectName.text = data.findCourseFolderProgress.folder?.name

            }.onFailure { error ->
                // Handle the error
                Log.e("AllDemoResourcesFree", error.message.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
