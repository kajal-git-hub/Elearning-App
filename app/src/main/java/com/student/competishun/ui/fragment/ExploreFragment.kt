package com.student.competishun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.core.widget.NestedScrollView
import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.student.competishun.R
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
import com.student.competishun.ui.viewmodel.GetCourseByIDViewModel
import com.student.competishun.utils.HelperFunctions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment(), OurContentAdapter.OnItemClickListener {

    private lateinit var binding: FragmentExploreBinding
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()
    private lateinit var combinedTabItems: List<TabItem>
    private lateinit var limitedFaqItems: List<FAQItem>
    private lateinit var faqItems: List<FAQItem>

    private var showMoreOrLess = ObservableField("View More")
    var isItemSize = ObservableField(true)

    private lateinit var helperFunctions: HelperFunctions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreBinding.inflate(inflater, container, false).apply {
            this.courseByIDViewModel = this@ExploreFragment.getCourseByIDViewModel
            lifecycleOwner = viewLifecycleOwner
            requireActivity().onBackPressedDispatcher.addCallback(this){
                handleBackPressed()
            }

        }
        return binding.root
    }
    private fun handleBackPressed() {
        findNavController().navigate(R.id.homeFragment)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clBuynow.setOnClickListener {
            findNavController().navigate(R.id.action_exploreFragment_to_myCartFragment)
        }
        binding.igInstallmentUp.setOnClickListener {

        binding.clInstallmentOptionView.setOnClickListener {
            val bottomSheet = InstallmentDetailsBottomSheet()
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

        val ourContentAdapter = OurContentAdapter(items, isItemSize, this)
        binding.rvOurContent.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = ourContentAdapter
        }
        val courseId = "250bceb2-45e4-488e-aa02-c9521555b424"
        getCourseByIDViewModel.fetchCourseById(courseId)

        getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner, Observer { courses ->

        })

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

        val courseFItems = listOf(
            CourseFItem("Question Papers with Detailed Solution", R.drawable.group_1272628766),
            CourseFItem("Tests & Quiz with Detailed Analysis", R.drawable.group_1272628766),
            CourseFItem("Question Papers with Detailed Solution", R.drawable.group_1272628766)
        )

        val courseFeaturesAdapter = CourseFeaturesAdapter(courseFItems)
        binding.rvCourseFeatures.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = courseFeaturesAdapter
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
        binding.backIv.setOnClickListener { requireActivity().onBackPressed() }

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

        val tabItems1 = listOf(
            TabItem(
                discount = "11% OFF",
                courseName = "Prakhar Integrated (Fast Lane-2) 2024-25",
                tags = listOf("12th Class", "Full-Year", "Target 2025"),
                startDate = "Starts On: 01 Jul, 24",
                endDate = "Expiry Date: 31 Jul, 24",
                lectures = "Lectures: 56",
                quizzes = "Quiz & Tests: 120",
                originalPrice = "₹44,939",
                discountPrice = "₹29,900"
            )
        )

        val tabItems2 = listOf(
            TabItem(
                discount = "15% OFF",
                courseName = "Medical Entrance Prep 2024-25",
                tags = listOf("12th Class", "Full-Year", "Target 2025"),
                startDate = "Starts On: 01 Aug, 24",
                endDate = "Expiry Date: 31 Aug, 24",
                lectures = "Lectures: 60",
                quizzes = "Quiz & Tests: 130",
                originalPrice = "₹50,000",
                discountPrice = "₹42,500"
            )
        )

        val tabItems3 = listOf(
            TabItem(
                discount = "20% OFF",
                courseName = "Engineering Entrance Prep 2024-25",
                tags = listOf("12th Class", "Full-Year", "Target 2025"),
                startDate = "Starts On: 01 Sep, 24",
                endDate = "Expiry Date: 31 Sep, 24",
                lectures = "Lectures: 70",
                quizzes = "Quiz & Tests: 140",
                originalPrice = "₹60,000",
                discountPrice = "₹48,000"
            )
        )

        combinedTabItems = tabItems1 + tabItems2 + tabItems3

        binding.rvRelatedCourses.adapter = CourseAdapter(combinedTabItems)

        helperFunctions.setupDotsIndicator(requireContext(), combinedTabItems.size, binding.llDotsRelatedCourse)
        helperFunctions.setupDotsIndicator(requireContext(), teacherItems.size, binding.llDotsIndicatorteachers)
        helperFunctions.setupDotsIndicator(requireContext(), courseFItems.size, binding.llDotsIndicatorFeatures)
        binding.rvRelatedCourses.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                helperFunctions.updateDotsIndicator(recyclerView, binding.llDotsRelatedCourse)
            }
        })
        binding.rvMeetTeachers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                helperFunctions.updateDotsIndicator(recyclerView, binding.llDotsIndicatorteachers)
            }
        })
        binding.rvCourseFeatures.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                helperFunctions.updateDotsIndicator(recyclerView, binding.llDotsIndicatorFeatures)
            }
        })
    }

    override fun onFirstItemClick() {
        findNavController().navigate(R.id.action_exploreFragment_to_demoFreeFragment)
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
}
