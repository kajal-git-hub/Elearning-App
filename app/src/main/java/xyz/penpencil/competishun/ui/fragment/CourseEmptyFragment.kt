package xyz.penpencil.competishun.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.student.competishun.curator.MyCoursesQuery
import com.student.competishun.gatekeeper.MyDetailsQuery
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.ExploreCourse
import xyz.penpencil.competishun.databinding.FragmentCourseEmptyBinding
import xyz.penpencil.competishun.ui.adapter.ExploreCourseAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.GetCourseByIDViewModel
import xyz.penpencil.competishun.ui.viewmodel.MyCoursesViewModel
import xyz.penpencil.competishun.ui.viewmodel.OrdersViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.utils.SharedPreferencesManager


@AndroidEntryPoint
class CourseEmptyFragment : Fragment() {

    private var _binding: FragmentCourseEmptyBinding? = null
    private val binding get() = _binding!!
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var exploreCourseAdapter:ExploreCourseAdapter
    private val ordersViewModel: OrdersViewModel by viewModels()
    private val viewModel: MyCoursesViewModel by viewModels()
    private var coursePercent=0
    private var currentCourseId=""
    private var folderId=""
    private var complementryId = ""
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()

    private var filteredCourseRequirements: Set<String> = emptySet()
    private val myCoursesViewModel: MyCoursesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Handle arguments if any
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCourseEmptyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.showBottomNavigationView(true)
        (activity as? HomeActivity)?.showFloatingButton(false)


        view.setFocusableInTouchMode(true)
        view.requestFocus()
        view.setOnKeyListener(object : View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    v?.findNavController()?.popBackStack()
                    return true
                }
                return false
            }

        })

        drawerLayout = (activity as HomeActivity).findViewById(R.id.drwaer_layout)

        observeUserDetails()
        userViewModel.fetchUserDetails()

        binding.menuIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val navigationView: NavigationView = (activity as HomeActivity).findViewById(R.id.nv_navigationView)
        val headerView = navigationView.getHeaderView(0)
        val igClose: ImageView = headerView.findViewById(R.id.ig_close)

        igClose.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }


//        binding.btnExploreCoursesEmpty.setOnClickListener {
//            findNavController().navigate(R.id.homeFragment)
//        }

        binding.btnExploreCoursesEmpty.setOnClickListener {
            (activity as? HomeActivity)?.findViewById<BottomNavigationView>(R.id.bottomNav)?.selectedItemId = R.id.home
            findNavController().navigate(R.id.homeFragment)
        }


        (activity as? HomeActivity)?.showBottomNavigationView(true)
        (activity as? HomeActivity)?.showFloatingButton(false)


        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        myCoursesBind()
       binding.profileIcon.setOnClickListener {
           findNavController().navigate(R.id.action_courseEmptyFragment_to_ProfileFragment)
       }


        // Log.e("userid  $userId: ",sharedPreferencesManager.userId.toString())
//        if (!sharedPreferencesManager.userId.isNullOrEmpty()) {
//            orderdetails(ordersViewModel,sharedPreferencesManager.userId.toString())
//        }else orderdetails(ordersViewModel,userId)

//        val exploreCourseList = listOf(
//            ExploreCourse("Prakhar Integrated (Fast Lane-2) 2024-25", "12th class", "Full-Year", "Target 2025", "Ongoing", 10),
//            ExploreCourse("Prakhar Integrated (Fast Lane-2) 2024-25", "Revision", "Full-Year", "Target 2025", "Completed", 100),
//            ExploreCourse("Prakhar Integrated (Fast Lane-2) 2024-25", "Revision", "Full-Year", "Target 2025", "Completed", 100)
//
//        )
//
//
//
//        val adapter = ExploreCourseAdapter(exploreCourseList) { course ->
//            findNavController().navigate(R.id.action_courseEmptyFragment_to_ResumeCourseFragment)
//        }
//
//        binding.rvExploreCourses.adapter = adapter
//        binding.rvExploreCourses.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        getMyDetails()
    }

//    private fun courseDetails(orders: List<OrdersByUserIdsQuery.OrdersByUserId>) {
//        val courseDetailsList = mutableListOf<ExploreCourse>()
//        getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner, Observer { course ->
//            course?.let {
//                Log.e("course", course.folder.toString())
//                var hasFreeFolder = false
//                course.folder?.forEach { folder ->
//                    if (folder.name.startsWith("Free")) {
//                        hasFreeFolder = true
//                    }
//                }
//
//                folderId = course.folder?.get(0)?.id.toString()
//
//                val courseClass = when (course.course_class.toString()) {
//                    "TWELFTH_PLUS" -> "12th+ Class"
//                    "TWELFTH" -> "12th Class"
//                    "ELEVENTH" -> "11th Class"
//                    else -> ""
//                }
//
//                val tag1 = courseClass
//                val tag2 = it.category_name.orEmpty()
//                courseDetailsList.add(
//                    ExploreCourse(
//                        it.name,
//                        tag1,
//                        tag2,
//                        "Target ${it.target_year}",
//                        it.status.toString(),
//                        coursepercent,
//                        hasFreeFolder
//                    )
//                )
//                if (courseDetailsList.size == orders.size) {
//                    binding.clEmptyMyCourse.visibility = View.GONE
//                    binding.rvExploreCourses.visibility = View.VISIBLE
//                    val adapter = ExploreCourseAdapter(courseDetailsList) { course ->
//                        val bundle = Bundle()
//                        bundle.putString("course_Id", currentCourseId)
//                        bundle.putString("folder_Id", folderId)
//                        Log.d("course_Id", currentCourseId)
//                        Log.d("folder_Idnamecouse1", folderId)
//                        findNavController().navigate(R.id.action_courseEmptyFragment_to_ResumeCourseFragment,bundle)
//                    }
//                    binding.rvExploreCourses.adapter = adapter
//                    binding.rvExploreCourses.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                }
//            }
//        })
//
//        orders.forEach { order ->
//            currentCourseId=order.entityId
//            Log.e("courseID",currentCourseId)
//            getCourseByIDViewModel.fetchCourseById(order.entityId)
//        }
//    }

    private fun observeUserDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val rollNo = data.getMyDetails.userInformation.rollNumber
                rollNo?.let {
                    sharedPreferencesManager.putString("ROLL_NUMBER", it)
                }
                if(rollNo!=null){
                    binding.etRollNo.text = "( ${rollNo} )"
                }else{
                    binding.etRollNo.text = ""
                }

                val fullName = data.getMyDetails.fullName
                val firstName = fullName?.split(" ")?.firstOrNull() ?: ""
                binding.welcomeUserTxt.text = "Hello, $firstName"
//
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun myCoursesBind() {
        binding.progressBar.visibility = View.VISIBLE
        binding.clEmptyMyCourse.visibility = View.GONE
        val courseDetailsList = mutableListOf<ExploreCourse>()
        viewModel.myCourses.observe(viewLifecycleOwner) { result ->
            binding.progressBar?.visibility = View.GONE
            Log.e("getMyresule", result.toString())
            result.onSuccess { data ->

                if (data.myCourses.isNotEmpty()) {
                    sharedPreferencesManager.putBoolean("isMyCourseAvailable", true)
                    sharedPreferencesManager.isBottomSheetShown = false
                    binding.progressBar.visibility = View.GONE
                    binding.clEmptyMyCourse.visibility = View.GONE
                    binding.rvExploreCourses.visibility = View.VISIBLE
                    // Create lists to hold courses and progress
                    val courseList = mutableListOf<MyCoursesQuery.Course>()
                    val progressList = mutableListOf<MyCoursesQuery.Progress>()
                    data.myCourses.forEach { myCourse ->
                        val course = myCourse.course
                        val progress = myCourse.progress

                        val selectedCourse = MyCoursesQuery.Course(
                            course_class = course.course_class,
                            id = course.id,
                            name = course.name,
                            price = course.price,
                            target_year = course.target_year,
                            status = course.status,
                            banner_image = course.banner_image,
                            other_requirements = course.other_requirements,
                            exam_type = course.exam_type,
                            category_name = course.category_name,
                            category_id = course.category_id,
                            complementary_course = course.complementary_course,
                            entity_type = course.entity_type,
                            folder = course.folder,
                            course_end_date = course.course_end_date,
                            course_start_date = course.course_start_date
                        )
                        val selectedProgress = MyCoursesQuery.Progress(
                            completionPercentage = progress?.completionPercentage ?: 0.0,
                            subfolderDurations = progress?.subfolderDurations,
                            folderCount = progress?.folderCount?:0.0
                        )

                        // Add course and progress to lists
                        courseList.add(selectedCourse)
                        progressList.add(selectedProgress)
                    }


                    // Create adapter with course and progress lists
                     exploreCourseAdapter = ExploreCourseAdapter(courseList, progressList) { course, folders, progressPercentage  ->
                        // Handle the course click and navigate
                        val gson = Gson()
                        val folderJson = gson.toJson(folders)
                        val courseJson = gson.toJson(course)
                        val progressPercentages = gson.toJson(progressPercentage)
                        Log.d("CourseClick", "Course: ${course.name}, Folders: $folders, FolderNames: $progressPercentage")

                        val bundle = Bundle().apply {
//
                            putString("folderJson", folderJson)
                            putString("courseJson", courseJson)
                           putString("courseName", course.name)
                            putString("courseId", course.id)
                            putString("courseStart",course.course_start_date.toString())
                            putString("courseEnd",course.course_end_date.toString())
                            putString("completionPercentages", progressPercentages)

                        }

                        findNavController().navigate(R.id.action_courseEmptyFragment_to_ResumeCourseFragment, bundle)
                    }

                    binding.rvExploreCourses.adapter = exploreCourseAdapter
                    binding.rvExploreCourses.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.clEmptyMyCourse.visibility = View.VISIBLE
                }
            }.onFailure {
             //   binding?.clEmptyMyCourse?.visibility = View.VISIBLE
                Log.e("MyCoursesFail", it.message.toString())
                Toast.makeText(requireContext(), "Failed to load courses: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchMyCourses()
    }



//    fun orderdetails(ordersViewModel: OrdersViewModel, userId:String
//    ){
//        val userIds = listOf(userId)
//        ordersViewModel.fetchOrdersByUserIds(userIds)
//        ordersViewModel.ordersByUserIds.observe(viewLifecycleOwner, Observer { orders ->
//
//            Log.d("orders",orders.toString())
//
//            binding.clEmptyMyCourse.visibility = View.VISIBLE
//            binding.welcomeUserTxt.text = "Hello, "+sharedPreferencesManager.name
//            orders?.let {
//                courseDetails(orders)
//            } ?: run {
//                binding.clEmptyMyCourse.visibility = View.VISIBLE
//                Log.e("courseEmpty", "No orders found")
//            }
//        })
//
//        // Fetch orders by user IDs
//
//    }


    override fun onResume() {
        super.onResume()
        setStatusBarGradiant(requireActivity())
        (activity as? HomeActivity)?.hideCallingSupport()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //for changing status bar text color
          /*  WindowInsetsControllerCompat(
                this,
                this.decorView
            ).isAppearanceLightStatusBars = false*/
        }
    }

    private fun setStatusBarGradiant(activity: Activity) {
        val window: Window = activity.window
        val background = ContextCompat.getDrawable(activity, R.drawable.explore_course_top_bar_bg)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity,android.R.color.transparent)
     /*   WindowInsetsControllerCompat(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = false*/
        window.setBackgroundDrawable(background)
    }



    fun getMyDetails() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                fetchCoursesAndUpdateUI(data.getMyDetails)
            }.onFailure { exception ->
                Log.e(TAG, "getMyDetails: " +exception.message)
            }
        }
        userViewModel.fetchUserDetails()
    }

    private fun fetchCoursesAndUpdateUI(
        userDetails: MyDetailsQuery.GetMyDetails
    ) {
        val missingPersonalFields = mutableListOf<String>().apply {
            if (!userDetails.fullName.isNullOrEmpty()) add("FULL_NAME")
            if (!userDetails.mobileNumber.isNullOrEmpty()) add("WHATSAPP_NUMBER")
            if (!userDetails.userInformation.fatherName.isNullOrEmpty()) add("FATHERS_NAME")
            if (!userDetails.userInformation.tShirtSize.isNullOrEmpty()) add("T_SHIRTS")
        }

        val missingDocumentFields = mutableListOf<String>().apply {
            if (!userDetails.userInformation.documentPhoto.isNullOrEmpty()) add("AADHAR_CARD")
            if (!userDetails.userInformation.passportPhoto.isNullOrEmpty()) add("PASSPORT_SIZE_PHOTO")
        }

        val missingAddressFields = userDetails.userInformation.address?.let { data ->
            if (data.pinCode != null || data.addressLine1 != null) {
                listOf("FULL_ADDRESS")
            } else {
                emptyList()
            }
        } ?: emptyList()

        myCoursesViewModel.myCourses.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val courseRequirements = data.myCourses
                    .flatMap { it.course.other_requirements.orEmpty() }
                    .map { it.rawValue }
                    .toHashSet()

                if (courseRequirements.contains("ALL")) return@onSuccess

                val allMissingFields =
                    missingPersonalFields + missingDocumentFields + missingAddressFields
                filteredCourseRequirements =
                    courseRequirements.filterNot { it in allMissingFields }.toSet()
                Log.e("fsdfasdfsdfsd", "fetchCoursesAndUpdateUI: $filteredCourseRequirements")
                if (filteredCourseRequirements.isNotEmpty()) {
                    findNavController().navigate(R.id.PersonalDetailsFragment, Bundle().apply {
                        putStringArray("FIELD_REQUIRED", filteredCourseRequirements.toTypedArray())
                    })
                }
            }.onFailure {
                Log.e("MyCoursesFail", it.message.toString())
            }
        }
        myCoursesViewModel.fetchMyCourses()
    }
}
