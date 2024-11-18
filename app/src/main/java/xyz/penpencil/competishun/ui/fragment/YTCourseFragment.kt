package xyz.penpencil.competishun.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.type.FindAllCourseInputStudent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentYTCourseBinding
import xyz.penpencil.competishun.ui.adapter.StudyCoursesAdapter
import xyz.penpencil.competishun.ui.adapter.YTCourseAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.GetCourseByIDViewModel
import xyz.penpencil.competishun.ui.viewmodel.StudentCoursesViewModel
import xyz.penpencil.competishun.utils.FilterSelectionListener
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import xyz.penpencil.competishun.utils.StudentCourseItemClickListener

@AndroidEntryPoint
class YTCourseFragment : Fragment(), FilterSelectionListener, StudentCourseItemClickListener {
    private lateinit var helperFunctions: HelperFunctions
    private lateinit var binding: FragmentYTCourseBinding
    private val filterOptions = listOf("IIT-JEE", "NEET")
    private val courseViewModel: StudentCoursesViewModel by viewModels()
    private val TAG = "YTCourseFragment"
    private lateinit var adapter: YTCourseAdapter
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()
    private lateinit var courseId: String
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private var autoSelectedExam = ""
    private var autoSelectedSubject = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYTCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? HomeActivity)?.showBottomNavigationView(true)
        (activity as? HomeActivity)?.showFloatingButton(true)
        helperFunctions = HelperFunctions()
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        binding.backIcon.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.tvCourseMaterialCount.text = "0 Courses"
        adapter = YTCourseAdapter(emptyList(), getCourseByIDViewModel, this@YTCourseFragment)
        binding.rvYTCourse.adapter = adapter
        setupSearch()
        fetchCoursesForClass("","")
        setupToggleRecyclerView()
        binding.tvFilterTextYT.setOnClickListener {
            val filterYTFragment = FilterStudyMaterialFragment().apply {
                val bundle = Bundle()
                bundle.putString("AUTO_SELECTED_SUBJECT", autoSelectedSubject)
                bundle.putString("AUTO_SELECTED_EXAM", autoSelectedExam)
                arguments = bundle
            }

            filterYTFragment.show(childFragmentManager, "FilterStudyMaterialFragment")
        }
    }

    private fun setupToggleRecyclerView() {
        Log.d("SelectedOpt", "Selected: $autoSelectedExam")

        val adapter = StudyCoursesAdapter(filterOptions,autoSelectedExam) { selectedOption ->
            Log.d("SelectedOption", "Selected: $selectedOption")
             autoSelectedExam = selectedOption
            val filters = FindAllCourseInputStudent(
                category_name = Optional.present("Youtube Courses"),
                exam_type = Optional.present(selectedOption)
            )
            courseViewModel.fetchCourses(filters)
            observeCourses()
        }
        binding.rvToggleButtonsYT.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvToggleButtonsYT.adapter = adapter
    }

    private fun fetchCoursesForClass(courseClass: String, selectedExam: String?) {
        val categoryName = arguments?.getString("category_name")
        val examType = arguments?.getString("exam_type")
        val filters = FindAllCourseInputStudent(
            category_name = Optional.present("Youtube Courses"),
           // course_class = Optional.present(courseClass),
           // exam_type = Optional.present(selectedExam)
        )
     //   courseViewModel.setCoursesEmpty()
        courseViewModel.fetchCourses(filters)
        observeCourses()
    }

    private fun observeCourses() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvYTCourse.visibility = View.GONE
        binding.tvShowingResults.visibility = View.GONE
        lifecycleScope.launch {
            courseViewModel.coursesState.collect { result ->
                result.onSuccess { data ->
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        courseViewModel.courses.collect { result ->
                            result?.onSuccess { data ->
                                binding.progressBar.visibility = View.GONE
                                binding.rvYTCourse.visibility = View.VISIBLE
                                binding.tvShowingResults.visibility = View.VISIBLE
                                Log.e(TAG, data.toString())
                                val courseSize = data.getAllCourseForStudent.courses.size
                                val courses = data.getAllCourseForStudent.courses
                                if (courses.isEmpty()) {
                                    binding.clNoEmptyView.visibility = View.VISIBLE
                                    binding.rvYTCourse.visibility = View.GONE
                                    binding.tvShowingResults.visibility = View.GONE
                                } else if (!courses.isEmpty()) {
                                    binding.clNoEmptyView.visibility = View.GONE
                                    binding.rvYTCourse.visibility = View.VISIBLE
                                    binding.tvShowingResults.visibility = View.VISIBLE
                                    binding.tvShowingResults.text = "Showing results (${courses.size})"
                                    courses.map { course ->

                                        val courseClass = course.course_class?.name ?: ""
                                        Log.e(
                                            "NEETcouseacal",
                                            helperFunctions.toDisplayString(courseClass)
                                        )
//                        when (helperFunctions.toDisplayString(courseClass)) {
//                            "11th" -> updateTabText(0, courseSize)
//                            "12th" -> updateTabText(1, courseSize)
//                            "12+" -> updateTabText(2, courseSize)
//                        }
                                        // course.toCourse()
                                    } ?: emptyList()
                                }
                                Log.d("NEETFragment", courses.toString())
                                binding.rvYTCourse.adapter = YTCourseAdapter(courses,getCourseByIDViewModel, this@YTCourseFragment)
                                adapter.updateData(courses)

                            }?.onFailure { exception ->
                                // Handle the failure case
                                binding.progressBar.visibility = View.GONE
                                Log.e(TAG, exception.toString())
                            }
                        }
                    }
                }

            }
        }
    }

    override fun onFiltersSelected(selectedExam: String?, selectedSubject: String?) {
        Log.e(TAG,selectedExam + selectedSubject)
        Log.d("Filters", "Selected $autoSelectedExam: $selectedExam, Selected Subject: $selectedSubject")
        if (selectedExam != null) {
            autoSelectedExam = selectedExam
            val adapter = StudyCoursesAdapter(filterOptions,autoSelectedExam) { selectedOption ->
                autoSelectedExam = selectedOption
                val filters = FindAllCourseInputStudent(
                    category_name = Optional.present("Youtube Courses"),
                    exam_type = Optional.present(selectedOption)
                )
                courseViewModel.fetchCourses(filters)
                observeCourses()
            }
            if (selectedSubject != null){ autoSelectedSubject = selectedSubject}
            binding.rvToggleButtonsYT.adapter = adapter
        }
        val filters = when {
            // If both selectedExam and selectedSubject are present
            ((selectedExam != null && selectedSubject != null) &&selectedExam != "" && selectedSubject != "" )-> FindAllCourseInputStudent(
                category_name = Optional.present("Youtube Courses"),
                exam_type = Optional.present(selectedExam),
                course_class = Optional.present(selectedSubject)
            )

            // If only selectedExam is present
            (selectedExam != null && selectedExam != ""&& autoSelectedSubject == "") -> {
                autoSelectedExam = selectedExam
                Log.e("autoslecteds",selectedExam)
                FindAllCourseInputStudent(
                    category_name = Optional.present("Youtube Courses"),
                    exam_type = Optional.present(selectedExam)
                )
            }
            (selectedExam == null && autoSelectedExam != ""  && selectedSubject==null) -> {
                autoSelectedSubject = ""

                Log.e("autoslecteds $selectedSubject",autoSelectedExam)
                FindAllCourseInputStudent(
                    category_name = Optional.present("Youtube Courses"),
                    exam_type = Optional.present(autoSelectedExam)
                )
            }

            // If only selectedSubject is present
            (selectedSubject != null && selectedSubject != "" && autoSelectedExam == "") -> {
                autoSelectedSubject = selectedSubject
                FindAllCourseInputStudent(
                    category_name = Optional.present("Youtube Courses"),
                    course_class = Optional.present(selectedSubject)
                )
            }
            (selectedSubject != null && autoSelectedExam != "" && selectedSubject != "") ->{
                Log.e("dfdddff","afdfjlk $autoSelectedExam")
                autoSelectedSubject = selectedSubject
                FindAllCourseInputStudent(
                    category_name = Optional.present("Youtube Courses"),
                    course_class = Optional.present(selectedSubject),
                            exam_type = Optional.present(autoSelectedExam),
                )
            }
            (selectedExam != null && autoSelectedSubject != "" && selectedExam != "") ->{
                Log.e("dfdddff","afdfjlk $autoSelectedExam")
                autoSelectedExam = selectedExam
                FindAllCourseInputStudent(
                    category_name = Optional.present("Youtube Courses"),
                    course_class = Optional.present(autoSelectedSubject),
                    exam_type = Optional.present(selectedExam),
                )
            }

            // Default case if neither is selected (optional, if needed)
            else -> {
                Log.e("filtersd $selectedExam",selectedSubject?:"")
                FindAllCourseInputStudent(
                    category_name = Optional.present("Youtube Courses"))
            }
        }

       filters.let {
            courseViewModel.fetchCourses(filters)
            observeCourses()
        }

    }

    override fun onCourseItemClicked(course: AllCourseForStudentQuery.Course, bundle: Bundle) {
        val courseTags = bundle.getStringArrayList("course_tags")?: arrayListOf()


        Log.e(TAG, course.id.toString())
        Log.e(TAG, "Course Tags: ${courseTags.toString()}")

        val newBundle = Bundle().apply {
            putString("course_id", course.id)
            putStringArrayList("course_tags", courseTags)
        }
        sharedPreferencesManager.putString("TOPIC_ID_STUDY", "")
        sharedPreferencesManager.putString("TOPIC_ID_STUDY_TYPE", "")
        findNavController().navigate(R.id.YTDetailsFragment, newBundle)
    }
    private fun setupSearch() {
        binding.searchIconYTCourse.queryHint = "Search"
        binding.searchIconYTCourse.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                Log.e("youtubvecourse",newText.toString())
                return true
            }
        })
    }
}