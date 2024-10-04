package xyz.penpencil.competishun.ui.fragment

import xyz.penpencil.competishun.ui.adapter.RecommendedCoursesAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.GetAllCourseCategoriesQuery
import com.student.competishun.curator.type.FindAllBannersInput
import com.student.competishun.curator.type.FindAllCourseInputStudent
import xyz.penpencil.competishun.data.model.PromoBannerModel
import xyz.penpencil.competishun.data.model.RecommendedCourseDataModel
import xyz.penpencil.competishun.data.model.Testimonial
import xyz.penpencil.competishun.data.model.WhyCompetishun
import xyz.penpencil.competishun.ui.adapter.OurCoursesAdapter
import xyz.penpencil.competishun.ui.adapter.PromoBannerAdapter
import xyz.penpencil.competishun.ui.adapter.TestimonialsAdapter
import xyz.penpencil.competishun.ui.adapter.WhyCompetishunAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.CoursesCategoryViewModel
import xyz.penpencil.competishun.ui.viewmodel.StudentCoursesViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel
import xyz.penpencil.competishun.ui.viewmodel.VerifyOtpViewModel
import xyz.penpencil.competishun.utils.Constants
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.OnCourseItemClickListener
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentHomeBinding
import xyz.penpencil.competishun.ui.main.MainActivity

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var testimonial_recyclerView: RecyclerView
    private lateinit var rvWhyCompetishun: RecyclerView
    private lateinit var courses: List<AllCourseForStudentQuery. Course>
    private val verifyOtpViewModel: VerifyOtpViewModel by viewModels()
    private lateinit var dotsIndicatorTestimonials: LinearLayout
    private lateinit var dotsIndicatorWhyCompetishun: LinearLayout
    private lateinit var adapter: TestimonialsAdapter
    private lateinit var adapterWhyCompetishun: WhyCompetishunAdapter
    private lateinit var testimonials: List<Testimonial>
    private lateinit var adapterRecommend: RecommendedCoursesAdapter
    private lateinit var listWhyCompetishun: List<WhyCompetishun>
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedPreferencesManager : SharedPreferencesManager
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private val coursesCategoryViewModel: CoursesCategoryViewModel by viewModels()
    private val studentCoursesViewModel: StudentCoursesViewModel by viewModels()
    private lateinit var rvOurCourses: RecyclerView
    private lateinit var dotsIndicatorOurCourses: LinearLayout
    private lateinit var adapterOurCourses: OurCoursesAdapter
    private var listOurCoursesItem: List<GetAllCourseCategoriesQuery.GetAllCourseCategory>? = null
    private val lectureCounts = mutableMapOf<String, Int>()
    private lateinit var recommendedCourseList: List<RecommendedCourseDataModel>
    private var filteredCourses: List<AllCourseForStudentQuery.Course> = listOf()


    private lateinit var helperFunctions: HelperFunctions

    private lateinit var contactImage: ImageView


    private val userViewModel: UserViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            //  this.coursesViewModel = this@HomeFragment.coursesViewModel
            this.studentCoursesViewModel = this@HomeFragment.studentCoursesViewModel
            this.coursesCategoryViewModel = this@HomeFragment.coursesCategoryViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? HomeActivity)?.showBottomNavigationView(true)
        (activity as? HomeActivity)?.showFloatingButton(true)

        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        Log.d("tokenn",sharedPreferencesManager.accessToken.toString())

        verifyOtpViewModel.verifyOtpResult.observe(viewLifecycleOwner) { result ->
            if (result==null)
            {
                findNavController().navigate(R.id.loginFragment)
            }
        }

        sharedPreferencesManager= SharedPreferencesManager(requireContext())
        getAllBanners()

        verifyOtpViewModel.verifyOtpResult.observe(viewLifecycleOwner) { result ->
            if (result==null)
            {
                findNavController().navigate(R.id.loginFragment)
            }
        }

        binding.tvRecommendViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_RecommendDetailFragment)
        }


        rvOurCourses = view.findViewById(R.id.rvOurCourses)
        dotsIndicatorOurCourses = view.findViewById(R.id.llDotsIndicatorOurCourses)
        rvOurCourses.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateDotsIndicator(recyclerView, dotsIndicatorOurCourses, 6)
            }
        })


        helperFunctions = HelperFunctions()

        drawerLayout = (activity as HomeActivity).findViewById(R.id.drwaer_layout)
        bottomNav = requireActivity().findViewById(R.id.bottomNav)
        val toolbar: MaterialToolbar = view.findViewById(R.id.topAppBar)
        contactImage = requireActivity().findViewById(R.id.ig_ContactImage)

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
//                R.id.action_notification -> {
//                    findNavController().navigate(R.id.action_homeFragment_to_NotificationFragment)
//                    true
//                }

                R.id.action_search -> {
                    true
                }

                R.id.action_profile -> {
                    findNavController().navigate(R.id.action_homeFragment_to_ProfileFragment)
                    true
                }

                else -> false
            }
        }

        toggle = ActionBarDrawerToggle(
            activity, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                bottomNav.visibility = View.GONE
                contactImage.visibility = View.GONE
            }

            override fun onDrawerClosed(drawerView: View) {
                bottomNav.visibility = View.VISIBLE
                contactImage.visibility = View.VISIBLE
            }
        })

        testimonial_recyclerView = view.findViewById(R.id.recyclerViewTestimonials)
        rvWhyCompetishun = view.findViewById(R.id.rvWhyCompetishun)
        dotsIndicatorTestimonials = view.findViewById(R.id.llDotsIndicator)
        dotsIndicatorWhyCompetishun = view.findViewById(R.id.llDotsIndicatorWhyCompetishun)


        _binding?.progressBar?.visibility = View.VISIBLE
        _binding?.rvOurCourses?.visibility = View.GONE
        binding.clRecommendedCourses.visibility = View.GONE
        coursesCategoryViewModel.coursesCategory.observe(viewLifecycleOwner, Observer { category ->
            _binding?.progressBar?.visibility = View.GONE

            if (!category.isNullOrEmpty()) {
                Log.e("coursesCategor not", category.toString())
                _binding?.rvOurCourses?.visibility = View.VISIBLE
                listOurCoursesItem = category
                adapterOurCourses = OurCoursesAdapter(listOurCoursesItem!!, object :
                    OnCourseItemClickListener {
                    override fun onCourseItemClick(course: GetAllCourseCategoriesQuery.GetAllCourseCategory) {
                        val bundle = Bundle().apply {
                            putString("course_name", course.name)
                            putString("category_id", course.id)

                        }
                        findNavController().navigate(R.id.action_homeFragment_to_coursesFragment, bundle)
                    }
                })
                rvOurCourses.adapter = adapterOurCourses
                rvOurCourses.layoutManager =
                    GridLayoutManager(context, 3, GridLayoutManager.HORIZONTAL, false)
                setupDotsIndicator(listOurCoursesItem!!.size, dotsIndicatorOurCourses, 6)
            }
        })
        coursesCategoryViewModel.fetchCoursesCategory()
        getMyDetails()
        getAllCoursesForStudent("")


        listWhyCompetishun = Constants.listWhyCompetishun
        testimonials = Constants.testimonials
        adapter = TestimonialsAdapter(testimonials)
        adapterWhyCompetishun = WhyCompetishunAdapter(listWhyCompetishun)
        testimonial_recyclerView.adapter = adapter
        rvWhyCompetishun.adapter = adapterWhyCompetishun
        testimonial_recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvWhyCompetishun.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        helperFunctions.setupDotsIndicator(
            requireContext(),
            testimonials.size,
            dotsIndicatorTestimonials
        )
        helperFunctions.setupDotsIndicator(
            requireContext(),
            listWhyCompetishun.size,
            dotsIndicatorWhyCompetishun
        )

        testimonial_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                helperFunctions.updateDotsIndicator(recyclerView, dotsIndicatorTestimonials)
            }
        })

        rvWhyCompetishun.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                helperFunctions.updateDotsIndicator(recyclerView, dotsIndicatorWhyCompetishun)
            }
        })


        val navigationView: NavigationView = (activity as HomeActivity).findViewById(R.id.nv_navigationView)
        val headerView = navigationView.getHeaderView(0)
        val igClose: ImageView = headerView.findViewById(R.id.ig_close)

        igClose.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.aboutUs ->{
                    findNavController().navigate(R.id.AboutUs)
//                    findNavController().navigate(R.id.AboutUs, null, NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build())
                }
                R.id.bookmark ->{
                    findNavController().navigate(R.id.BookMarkFragment)
                }
                R.id.download ->{
                    findNavController().navigate(R.id.DownloadFragment)
                }
                R.id.ContactUs -> {
                    findNavController().navigate(R.id.ContactUs)
//                    findNavController().navigate(R.id.ContactUs, null, NavOptions.Builder().setPopUpTo(R.id.homeFragment, true).build())

                }
                R.id.tvTermsPrivacy ->  {
                    findNavController().navigate(R.id.TermsAndCondition)
                }
                R.id.privacyPolicy ->{
                    findNavController().navigate(R.id.PolicyFragment)
                }
                R.id.tvdisclaimer ->{
                    findNavController().navigate(R.id.DisclaimerFragment)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true

        }

    }

    fun getAllBanners() {
        val filtersbanner = FindAllBannersInput(
            limit = Optional.Absent
        )
        studentCoursesViewModel.fetchBanners(filtersbanner)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            studentCoursesViewModel.banners.collect { result ->
                val bannerList = mutableListOf<PromoBannerModel>()
                Log.d("bannerList", bannerList.toString())
                result?.forEach { bannerlist ->
                    bannerlist?.let {
                        // Assuming you have course_id in the banner data
                        bannerList.add(PromoBannerModel(it.mobile_banner_image, it.redirect_link, it.course_id))
                    }
                }

                val adapter = PromoBannerAdapter(bannerList) { redirectLink, courseId ->
                    if (!redirectLink.isNullOrEmpty()) {
                        openLink(redirectLink)
                    } else if (!courseId.isNullOrEmpty()) {
                        val bannerCourseTag = listOf<String>()
                        Log.d("bannerCourseTag",bannerCourseTag.toString())

                        val bundle = Bundle().apply {
                            putString("course_id", courseId)
                        }
                        findNavController().navigate(R.id.exploreFragment, bundle)
                    } else {
                        Toast.makeText(requireContext(), "No redirect or course available for this banner", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.rvpromobanner.adapter = adapter
                binding.rvpromobanner.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                // Setup dots indicator
                helperFunctions.setupDotsIndicator(
                    requireContext(),
                    bannerList.size,
                    binding.llDotsIndicatorPromoBanner
                )

                // Set up the scroll listener for updating the dots indicator
                binding.rvpromobanner.addOnScrollListener(object :
                    RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        helperFunctions.updateDotsIndicator(
                            recyclerView,
                            binding.llDotsIndicatorPromoBanner
                        )
                    }
                })
            }
        }
    }



    fun getAllCoursesForStudent(courseType: String) {
        var courseTypes = courseType
        if (courseType != "IIT-JEE" || courseType != "NEET") {
            courseTypes = "IIT-JEE"
        }
        Log.e("cousetyeps",courseTypes)
        val filters = FindAllCourseInputStudent(
            category_name = Optional.Absent,
            course_class = Optional.Absent,
            exam_type = Optional.present(courseTypes),
            is_recommended = Optional.present(true)
        )
        studentCoursesViewModel.fetchCourses(filters)

        binding.progressBarRec.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            studentCoursesViewModel.courses.collect { result ->
                Log.e("TotalStudentCours", result.toString())
                result?.onSuccess { data ->
                    Log.e("TotalStudentCourses", data.toString())
                     courses = data.getAllCourseForStudent.courses.map { course ->
                        binding.progressBarRec.visibility = View.GONE
                        binding.clRecommendedCourses.visibility = View.VISIBLE
                        getAllLectureCount(course.id) { courseId, lectureCount ->
                            lectureCounts[courseId] = lectureCount
                            binding.rvRecommendedCourses.adapter?.notifyDataSetChanged()
                        }
                        AllCourseForStudentQuery.Course(
                            discount = course.discount,
                            name = course.name,
                            course_start_date = course.course_start_date,
                            course_validity_end_date = course.course_validity_end_date,
                            price = course.price,
                            target_year = course.target_year,
                            id = course.id,
                            academic_year = course.academic_year,
                            complementary_course = course.complementary_course,
                            course_features = course.course_features,
                            course_class = course.course_class,
                            course_tags = course.course_tags,
                            banner_image = course.banner_image,
                            status = course.status,
                            category_id = course.category_id,
                            category_name = course.category_name,
                            course_primary_teachers = course.course_primary_teachers,
                            course_support_teachers = course.course_support_teachers,
                            course_type = course.course_type,
                            live_date = course.live_date,
                            entity_type = course.entity_type,
                            exam_type = course.exam_type,
                            planner_description = course.planner_description,
                            with_installment_price = course.with_installment_price,
                            course_end_date = course.course_end_date,
                        )
                    } ?: emptyList()

                    adapterRecommend = courses.let { courseList ->
                        RecommendedCoursesAdapter(courseList, lectureCounts) { selectedCourse,recommendCourseTags ->
                            Log.d("recommendCoursesTags", recommendCourseTags.toString())
                            filteredCourses = courseList
                            Log.d("filteredCoursesHome", filteredCourses.toString())
                            val lectureCount = lectureCounts[selectedCourse.id]?.toString() ?: "0"
                            val bundle = Bundle().apply {
                                putString("course_id", selectedCourse.id)
                                putString("LectureCount", lectureCount)
                                putStringArrayList("recommendCourseTags", ArrayList(recommendCourseTags)) // Pass the tags

                            }
                            findNavController().navigate(R.id.exploreFragment, bundle)
                        }
                    }

                    binding.rvRecommendedCourses.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    binding.rvRecommendedCourses.adapter = adapterRecommend
                    setupToolbar()
                    recommendedCourseList = Constants.recommendedCourseList
                    adapterRecommend.updateCourses(courses)
                    helperFunctions.setupDotsIndicator(
                        requireContext(),
                        recommendedCourseList.size,
                        binding.llDotsIndicatorRecommendedCourses
                    )
                    binding.rvRecommendedCourses.addOnScrollListener(object :
                        RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            helperFunctions.updateDotsIndicator(
                                recyclerView,
                                binding.llDotsIndicatorRecommendedCourses
                            )
                        }
                    })
                }?.onFailure { exception ->
                    if(exception.message == "Unauthorized"){
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.putExtra("navigateToLogin", true)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    Log.e("gettiStudentfaik", exception.toString())
                }
            }
        }
    }

    private fun setupToolbar() {
        val searchView = binding.topAppBar.menu.findItem(R.id.action_search)?.actionView as? SearchView
        searchView?.queryHint = "Search Recommended Courses"
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {

                adapterRecommend.filter.filter(newText)
              //  adapterRecommend.updateCourses(filteredCourses)

                return true
            }
        })
    }

    fun getMyDetails() {
        userViewModel.fetchUserDetails()
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                val userDetails = data.getMyDetails
                Log.e("courseeTypehome", userDetails.userInformation.address.toString())
                sharedPreferencesManager.name=userDetails.fullName
                sharedPreferencesManager.city=userDetails.userInformation.address?.city
                sharedPreferencesManager.reference=userDetails.userInformation.reference
                sharedPreferencesManager.preparingFor=userDetails.userInformation.preparingFor
                sharedPreferencesManager.targetYear=userDetails.userInformation.targetYear
                var courseType = userDetails.userInformation.preparingFor ?: ""
                sharedPreferencesManager.name=userDetails.fullName
                sharedPreferencesManager.city=userDetails.userInformation.address?.city
                sharedPreferencesManager.reference=userDetails.userInformation.reference
                sharedPreferencesManager.preparingFor=userDetails.userInformation.preparingFor
                sharedPreferencesManager.targetYear=userDetails.userInformation.targetYear
                getAllCoursesForStudent(courseType)
                Log.e("courseeTypehome", courseType)

            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error fetching details: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

        fun getAllLectureCount(courseId: String, callback: (String, Int) -> Unit){

        studentCoursesViewModel.fetchLectures(courseId)
        Log.e("getcourseIds",courseId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                studentCoursesViewModel.lectures.collect { result ->
                    result?.onSuccess { lecture ->
                        val count = lecture.getAllCourseLecturesCount.lecture_count.toInt()
//                        Log.e("lectureCount",count.toString())
                        callback(courseId, count)
                    }?.onFailure { exception ->
                        Log.e("LectureException", exception.toString())
                    }
                }
            }
        }
    }


    private fun setupDotsIndicator(
        itemCount: Int,
        dotsIndicator: LinearLayout,
        spanCount: Int = 6
    ) {
        val pageCount = (itemCount + spanCount - 1) / spanCount
        dotsIndicator.removeAllViews()
        for (i in 0 until pageCount) {
            val dot = ImageView(requireContext())
            dot.setImageResource(R.drawable.dot_inactive)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)
            dot.layoutParams = params
            dotsIndicator.addView(dot)
        }
        updateDotsIndicator(null, dotsIndicator, spanCount)
    }


    private fun updateDotsIndicator(
        recyclerView: RecyclerView?,
        dotsIndicator: LinearLayout,
        spanCount: Int = 6
    ) {
        recyclerView?.let {
            val visiblePageIndex =
                (it.layoutManager as? GridLayoutManager)?.findFirstVisibleItemPosition()
                    ?.div(spanCount) ?: 0

            for (i in 0 until dotsIndicator.childCount) {
                val dot = dotsIndicator.getChildAt(i) as ImageView
                dot.setImageResource(
                    if (i == visiblePageIndex) R.drawable.dot_active
                    else R.drawable.dot_inactive
                )
            }
        }
    }


    private fun openLink(redirectLink: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectLink))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        drawerLayout.removeDrawerListener(toggle)
    }


}