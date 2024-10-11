package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.type.FindAllCourseInputStudent
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentStudyMaterialBinding
import xyz.penpencil.competishun.ui.adapter.CourseAdapter
import xyz.penpencil.competishun.ui.adapter.ExampleAdapter
import xyz.penpencil.competishun.ui.adapter.StudyCoursesAdapter
import xyz.penpencil.competishun.ui.adapter.StudyMaterialAdapter
import xyz.penpencil.competishun.ui.fragment.CourseFragment.Companion
import xyz.penpencil.competishun.ui.viewmodel.GetCourseByIDViewModel
import xyz.penpencil.competishun.ui.viewmodel.StudentCoursesViewModel
import xyz.penpencil.competishun.utils.Constants
import xyz.penpencil.competishun.utils.Constants.BOARD
import xyz.penpencil.competishun.utils.Constants.IIT_JEE
import xyz.penpencil.competishun.utils.Constants.NEET
import xyz.penpencil.competishun.utils.Constants.OTHERS
import xyz.penpencil.competishun.utils.Constants.UCET
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import xyz.penpencil.competishun.utils.StudentCourseItemClickListener


@AndroidEntryPoint
class StudyMaterialFragment : Fragment(), StudentCourseItemClickListener {
    val TAG = "StudyMaterialFragment"
    private lateinit var binding : FragmentStudyMaterialBinding
    private val courseViewModel: StudentCoursesViewModel by viewModels()
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()
    private lateinit var toggleButtonAdapter: StudyCoursesAdapter
    private val filterOptions = listOf("IIT-JEE", "NEET")
    private lateinit var helperFunctions: HelperFunctions
    private var selectedItem: String? = null
    private lateinit var sharedPreferencesManager : SharedPreferencesManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudyMaterialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helperFunctions = HelperFunctions()
        sharedPreferencesManager= SharedPreferencesManager(requireContext())
        binding.rvStudyMaterial.layoutManager = LinearLayoutManager(context)
        setupToggleRecyclerView()
        fetchCoursesForClass("","")
        binding.clFilterText.setOnClickListener {
            val filterStudyMaterialFragment = FilterStudyMaterialFragment()
            filterStudyMaterialFragment.show(childFragmentManager, "FilterStudyMaterialFragment")

        }

    }

    private fun setupToggleRecyclerView() {
        val adapter = StudyCoursesAdapter(filterOptions) { selectedOption ->
            // Handle item click (IIT_JEE or NEET)
            Log.d("SelectedOption", "Selected: $selectedOption")
            // Perform your actions based on the selected option
        }

        binding.rvToggleButtonsSM.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvToggleButtonsSM.adapter = adapter
    }
    private fun fetchCoursesForClass(courseClass: String, examType: String) {

            val filters = FindAllCourseInputStudent(
                category_name = Optional.present("Study Material")
            )
            courseViewModel.fetchCourses(filters)
            observeCourses()
    }

    private fun observeCourses() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            courseViewModel.courses.collect { result ->
                result?.onSuccess { data ->
                    Log.e(TAG, data.toString())
                    val courseSize = data.getAllCourseForStudent.courses.size

                    val courses = data.getAllCourseForStudent.courses.map { course ->

                        val courseClass = course.course_class?.name?:""
                        Log.e(TAG+ "1",helperFunctions.toDisplayString(courseClass))
//                        when (helperFunctions.toDisplayString(courseClass)) {
//                            "11th" -> updateTabText(0, courseSize)
//                            "12th" -> updateTabText(1, courseSize)
//                            "12+" -> updateTabText(2, courseSize)
//                        }
                       course
                    } ?: emptyList()
                    Log.d(TAG, courses.toString())


                    binding.rvStudyMaterial.adapter = StudyMaterialAdapter(courses,getCourseByIDViewModel)
                }?.onFailure { exception ->
                    // Handle the failure case
                    Log.e(TAG, exception.toString())
                }
            }
        }
    }

    override fun onCourseItemClicked(course: AllCourseForStudentQuery.Course, bundle: Bundle) {
        TODO("Not yet implemented")
    }

}