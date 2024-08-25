package com.student.competishun.ui.fragment

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.core.widget.NestedScrollView
import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout
import com.student.competishun.R
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.GetCourseByIdQuery
import com.student.competishun.curator.type.CreateCartItemDto
import com.student.competishun.curator.type.EntityType
import com.student.competishun.curator.type.FindAllCourseInputStudent
import com.student.competishun.data.model.CourseFItem
import com.student.competishun.data.model.FAQItem
import com.student.competishun.data.model.OtherContentItem
import com.student.competishun.data.model.OurContentFirstItem
import com.student.competishun.data.model.OurContentItem
import com.student.competishun.data.model.TabItem
import com.student.competishun.data.model.TeacherItem
import com.student.competishun.databinding.FragmentExploreBinding
import com.student.competishun.ui.adapter.CourseAdapter
import com.student.competishun.ui.adapter.CourseFeaturesAdapter
import com.student.competishun.ui.adapter.FAQAdapter
import com.student.competishun.ui.adapter.OurContentAdapter
import com.student.competishun.ui.adapter.TeacherAdapter
import com.student.competishun.ui.viewmodel.CoursesViewModel
import com.student.competishun.ui.viewmodel.CreateCartViewModel
import com.student.competishun.ui.viewmodel.GetCourseByIDViewModel
import com.student.competishun.ui.viewmodel.StudentCoursesViewModel
import com.student.competishun.utils.HelperFunctions
import com.student.competishun.utils.SharedPreferencesManager
import com.student.competishun.utils.StudentCourseItemClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment(), OurContentAdapter.OnItemClickListener,
    StudentCourseItemClickListener {

    private lateinit var binding: FragmentExploreBinding
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()
    private lateinit var combinedTabItems: List<TabItem>
    private lateinit var limitedFaqItems: List<FAQItem>
    private lateinit var faqItems: List<FAQItem>
    private val createCartViewModel: CreateCartViewModel by viewModels()
    private val cartViewModel: CreateCartViewModel by viewModels()
    private val courseViewModel: StudentCoursesViewModel by viewModels()
    private var showMoreOrLess = ObservableField("View More")
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    var isItemSize = ObservableField(true)
    private lateinit var courseId:String
    lateinit var folderlist:List<GetCourseByIdQuery.Folder>
    private lateinit var helperFunctions: HelperFunctions
    var firstInstallment:Double = 0.0
    var secondInstallment:Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreBinding.inflate(inflater, container, false).apply {
            this.courseByIDViewModel = this@ExploreFragment.getCourseByIDViewModel
            lifecycleOwner = viewLifecycleOwner

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val mediaController = MediaController(context)
        binding.videoView.setMediaController(mediaController)

        getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner) { course ->
            course?.let {
                val imageUrl = it.video_thumbnail
                val videoUrl = it.orientation_video

                Log.d("CourseVideoThumbnail", imageUrl ?: "No URL")
                Log.d("CourseOrientThumbnail", videoUrl ?: "No URL")

                // Display the image thumbnail
                downloadAndDisplayImage(imageUrl, binding.ivBannerExplore)

                // Handle ImageView click to play video
                binding.ivBannerExplore.setOnClickListener {
                    binding.ivBannerExplore.visibility = View.GONE
                    binding.videoView.visibility = View.VISIBLE

                    // Set the video URI and start playing
                    binding.videoView.setVideoURI(Uri.parse(videoUrl))
                    binding.videoView.start()

                    // Set up a listener for when the video completes
                    binding.videoView.setOnCompletionListener {
                        binding.videoView.visibility = View.GONE
                        binding.ivBannerExplore.visibility = View.VISIBLE
                    }
                }
            }
        }

        folderlist = emptyList()
        helperFunctions= HelperFunctions()
        combinedTabItems = listOf()
         courseId = arguments?.getString("course_id").toString()
        sharedPreferencesManager = SharedPreferencesManager(requireContext())
        val items = mutableListOf(
            OurContentItem.FirstItem(
                OurContentFirstItem(
                    R.drawable.frame_1707480918,
                    "Demo Resources",
                    R.drawable.group_1272628768
                )
            ),
            OurContentItem.OtherItem(
                OtherContentItem(
                    R.drawable.frame_1707480918,
                    "Preparation Mantra",
                    R.drawable.lock
                )
            ),
            OurContentItem.OtherItem(
                OtherContentItem(
                    R.drawable.frame_1707480918,
                    "Lectures",
                    R.drawable.lock
                )
            ),
            OurContentItem.OtherItem(
                OtherContentItem(
                    R.drawable.frame_1707480918,
                    "Tests",
                    R.drawable.lock
                )
            ),
            OurContentItem.OtherItem(
                OtherContentItem(
                    R.drawable.frame_1707480918,
                    "Study Materials",
                    R.drawable.lock
                )
            ),
        )
        val ourContentAdapter = OurContentAdapter(folderlist, isItemSize, this)
        binding.rvOurContent.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = ourContentAdapter
        }

        if (courseId.isEmpty()){
            Log.e("courseEmpty",courseId)
            binding.progressBar.visibility = View.VISIBLE
        }else {
            Log.e("courseID",courseId)
            getCourseByIDViewModel.fetchCourseById(courseId)
            getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner, Observer { courses ->
                Log.e("listcourses", courses.toString())
                binding.progressBar.visibility = View.GONE
                if (courses != null) {
                    Log.e("listcourses", courses.toString())
                    var coursefeature = courses.course_features
                    val courseFItems = coursefeature?.map { feature ->
                        CourseFItem(
                            featureText = formatFeatureText(feature),
                            bottomimage = R.drawable.group_1272628766
                        )
                    } ?: emptyList()

                    helperFunctions.setupDotsIndicator(
                        requireContext(),
                        courseFItems.size,
                        binding.llDotsIndicatorFeatures
                    )
                    val courseItems = listOf(
                        CourseFItem("Question Papers with Detailed Solution", R.drawable.group_1272628766),
                        CourseFItem("Tests & Quiz with Detailed Analysis", R.drawable.group_1272628766),
                        CourseFItem("Question Papers with Detailed Solution", R.drawable.group_1272628766)
                    )

                    val courseFeaturesAdapter = CourseFeaturesAdapter(courseFItems)
                    binding.rvCourseFeatures.apply {
                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = courseFeaturesAdapter
                    }
                    folderlist = courses.folder ?: emptyList()
                    val sortedFolderList = folderlist.sortedByDescending {
                        it.name.startsWith("Free")
                    }
                    binding.tvOurContentNumber.text = folderlist.size.toString() + " Total"

                    val coursePrice = courses.price?.toDouble() ?: 0.0
                    val discount = courses.discount?.toDouble() ?: 0.0
                    val installmentPrice = courses.with_installment_price?.toDouble() ?: 0.0

                    firstInstallment = ((coursePrice + installmentPrice) - discount) * 0.6
                     secondInstallment = (coursePrice - firstInstallment)
                    Log.e("secon $installmentPrice $coursePrice",secondInstallment.toString())

                    binding.tvCourseName.text = courses.name
                    binding.orgPricexp.text = "₹"+courses.price.toString()
                    val disountprice = ((courses.price?:0)-((courses.discount?:0)))
                    binding.dicountPricexp.text = "₹${disountprice}"
                    binding.tvStartDate.text = "Starts On: "+helperFunctions.formatCourseDate(courses.course_start_date.toString())
                    binding.tvEndDate.text ="Expiry Date: "+helperFunctions.formatCourseDate(courses.course_validity_end_date.toString())

                    val newItems = courses.folder?.map { folder -> mapFolderToOurContentItem(folder) } ?: emptyList()
                     val freeCourse = courses.folder?.get(0)?.name?.split(" ")?.get(0)
                    Log.e("getFreeCourse",freeCourse.toString())

                    ourContentAdapter.updateItems(sortedFolderList)
                }

            })
        }
        // Use the courseId as needed
        Log.d("ExploreFragmentId", "Received course ID: $courseId")
        binding.backIv.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.clBuynow.setOnClickListener {
            // Prepare data for API call

            val cartItems = listOf(CreateCartItemDto(
                courseId,
                EntityType.COURSE,1
            )) // Replace with actual cart items data

            // Call the API to create cart items
            createCartViewModel.createCartItems(sharedPreferencesManager.userId.toString(), cartItems)

            // Observe the result and navigate based on success or failure
            createCartViewModel.cartItemsResult.observe(viewLifecycleOwner, Observer { result ->
                result.onSuccess {

                    findNavController().navigate(R.id.action_exploreFragment_to_myCartFragment)
                }.onFailure { exception ->
                    // Handle error, e.g., show a toast or dialog
                    Toast.makeText(requireContext(), "Error creating cart items: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            })
        }


            binding.clInstallmentOptionView.setOnClickListener {


                val bottomSheet = InstallmentDetailsBottomSheet().apply {
                    setInstallmentData(firstInstallment.toInt(), secondInstallment.toInt())
                }
                bottomSheet.show(parentFragmentManager, "InstallmentDetailsBottomSheet")
            }

            helperFunctions = HelperFunctions()

            binding.tvReadMore.setOnClickListener {
                if (binding.tvCourseDescription.maxLines == 2) {
                    binding.tvCourseDescription.maxLines = Integer.MAX_VALUE
                    binding.tvCourseDescription.ellipsize = null
                    binding.tvReadMore.text = "Read Less"
                    binding.tvReadMore.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.arrow_up_explore, 0
                    )
                } else {
                    binding.tvCourseDescription.maxLines = 2
                    binding.tvCourseDescription.ellipsize = android.text.TextUtils.TruncateAt.END
                    binding.tvReadMore.text = "Read More"
                    binding.tvReadMore.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.arrow_down, 0
                    )
                }
            }




            binding.tvOurContentSeeMore.setOnClickListener {
                if (showMoreOrLess.get() == "View More") {
                    showMoreOrLess.set("View Less")
                    binding.tvOurContentSeeMore.text = "View Less"
                    isItemSize.set(false) // Show all items
                } else {
                    showMoreOrLess.set("View More")
                    binding.tvOurContentSeeMore.text = "View More"
                    isItemSize.set(true) // Show only 3 items
                }
                ourContentAdapter.notifyDataSetChanged()
            }


            val teacherItems = listOf(
                TeacherItem(R.drawable.teacher_bg, "Alok Srivastav", "Mathematics"),
                TeacherItem(R.drawable.teacher_bg, "Alok Srivastav", "Physics"),
                TeacherItem(R.drawable.teacher_bg, "Alok Srivastav", "Chemistry"),
            )
            val teacherAdapter = TeacherAdapter(teacherItems)
            binding.rvMeetTeachers.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = teacherAdapter
            }



            faqItems = listOf(
                FAQItem("Is this course have live online lectures?"),
                FAQItem("Is installments option available?"),
                FAQItem("Do you have refund policy?"),
                FAQItem("Do you have cancellation policy?"),
                FAQItem("How can I access the course material?"),
                FAQItem("What is the duration of the course?"),
                FAQItem("Are there any prerequisites for the course?")
            )
            limitedFaqItems = faqItems.take(4)

            val faqAdapter = FAQAdapter(limitedFaqItems)

            binding.rvFaq.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = faqAdapter
            }

            binding.clFAQViewAllText.findViewById<TextView>(R.id.etfaqViewAll).setOnClickListener {
                navigateToFaqFragment()
            }

            val tabLayout = binding.tabTablayout
            val nestedScrollView = binding.nestedScrollView
            val clOurContent = binding.clOurContent
            val clCourseFeatures = binding.clCourseFeatures
            val clCoursePlanner = binding.clCoursePlanner
            val clTeachers = binding.clTeachers

            tabLayout.addTab(tabLayout.newTab().setText("Content"))
            tabLayout.addTab(tabLayout.newTab().setText("Features"))
            tabLayout.addTab(tabLayout.newTab().setText("Planner"))
            tabLayout.addTab(tabLayout.newTab().setText("Teachers"))


            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    when (tab.position) {
                        0 -> nestedScrollView.scrollTo(0, clOurContent.top)
                        1 -> nestedScrollView.scrollTo(0, clCourseFeatures.top)
                        2 -> nestedScrollView.scrollTo(0, clCoursePlanner.top)
                        3 -> nestedScrollView.scrollTo(0, clTeachers.top)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabReselected(tab: TabLayout.Tab) {
                    when (tab.position) {
                        0 -> nestedScrollView.scrollTo(0, clOurContent.top)
                        1 -> nestedScrollView.scrollTo(0, clCourseFeatures.top)
                        2 -> nestedScrollView.scrollTo(0, clCoursePlanner.top)
                        3 -> nestedScrollView.scrollTo(0, clTeachers.top)
                    }
                }
            })

            val filters = FindAllCourseInputStudent(
                Optional.present("Complimentary Course"),
                Optional.present("IIT-JEE"),
                Optional.present(null),
                Optional.present(null)
            )
            courseViewModel.fetchCourses(filters)

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                courseViewModel.courses.collect { result ->
                    result?.onSuccess { data ->
                        Log.e("gettiStudent", data.toString())
                        val courses = data.getAllCourseForStudent.courses.map { course ->
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
                                entity_type = course.entity_type,
                                exam_type = course.exam_type,
                                planner_description = course.planner_description,
                                with_installment_price = course.with_installment_price
                            )
                        } ?: emptyList()
                        binding.rvRelatedCourses.adapter =
                            CourseAdapter(courses, this@ExploreFragment)
                    }?.onFailure { exception ->
                        // Handle the failure case
                        Log.e("gettiStudentfaik", exception.toString())
                    }
                }
            }




                helperFunctions.setupDotsIndicator(
                    requireContext(),
                    combinedTabItems.size,
                    binding.llDotsRelatedCourse
                )

            helperFunctions.setupDotsIndicator(
                requireContext(),
                teacherItems.size,
                binding.llDotsIndicatorteachers
            )

            binding.rvRelatedCourses.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    helperFunctions.updateDotsIndicator(recyclerView, binding.llDotsRelatedCourse)
                }
            })

            binding.rvMeetTeachers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    helperFunctions.updateDotsIndicator(
                        recyclerView,
                        binding.llDotsIndicatorteachers
                    )
                }
            })
            binding.rvCourseFeatures.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    helperFunctions.updateDotsIndicator(
                        recyclerView,
                        binding.llDotsIndicatorFeatures
                    )
                }
            })

    }

    override fun onFirstItemClick(folderId: String,folderName: String) {
        val bundle = Bundle().apply {
            putString("folderId", folderId)
            putString("folderName", folderName)
        }
        findNavController().navigate(R.id.action_exploreFragment_to_demoFreeFragment, bundle)
    }

    fun formatFeatureText(feature: String): String {
        return feature
            .split("_")
            .joinToString(" ") { it.toLowerCase().capitalize() }
    }

    override fun onOtherItemClick(folderId: String,folderName: String) {
//        val bundle = Bundle().apply {
//            putString("folderId", folderId)
//            putString("folderName", folderName)
//        }
//       findNavController().navigate(R.id.action_exploreFragment_to_demoFreeFragment,bundle)
    }
    private fun downloadAndDisplayImage(url: String?, imageView: ImageView) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    imageView.setImageBitmap(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
//                    imageView.setImageResource(R.drawable.error_image) // Optional: Handle error with a fallback image
                }
            })
    }


    private fun navigateToFaqFragment() {
        val bundle = Bundle().apply {
            putParcelableArrayList("faq_items", ArrayList(faqItems))
        }
        findNavController().navigate(R.id.action_exploreFragment_to_allFaqFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun mapFolderToOurContentItem(folder: GetCourseByIdQuery.Folder): OurContentItem {
        val isFreeCourse = folder.name.split(" ")[0].equals("Free", ignoreCase = true)
        val drawableRes = if (isFreeCourse) R.drawable.group_1272628768 else R.drawable.lock
       Log.e("getFolderID ${folder.id}", "courseID"+ folder.course_id)
        return OurContentItem.OtherItem(
            OtherContentItem(
                R.drawable.frame_1707480918,
                folder.name,
                drawableRes
            )
        )
    }

    override fun onCourseItemClicked(course: AllCourseForStudentQuery.Course) {
        val bundle = Bundle().apply {
            putString("course_id", course.id)
        }
        findNavController().navigate(R.id.action_coursesFragment_to_ExploreFragment,bundle)
    }
}
