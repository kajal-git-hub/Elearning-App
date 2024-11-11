package xyz.penpencil.competishun.ui.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo3.api.Optional
import com.bumptech.glide.Glide
import androidx.media3.common.MediaItem
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.GetCourseByIdQuery
import com.student.competishun.curator.MyCoursesQuery
import com.student.competishun.curator.type.CreateCartItemDto
import com.student.competishun.curator.type.EntityType
import com.student.competishun.curator.type.FindAllCourseInputStudent
import xyz.penpencil.competishun.data.model.CourseFItem
import xyz.penpencil.competishun.data.model.FAQItem
import xyz.penpencil.competishun.data.model.OtherContentItem
import xyz.penpencil.competishun.data.model.OurContentItem
import xyz.penpencil.competishun.data.model.TabItem
import xyz.penpencil.competishun.data.model.TeacherItem
import xyz.penpencil.competishun.ui.adapter.CourseAdapter
import xyz.penpencil.competishun.ui.adapter.CourseFeaturesAdapter
import xyz.penpencil.competishun.ui.adapter.FAQAdapter
import xyz.penpencil.competishun.ui.adapter.OurContentAdapter
import xyz.penpencil.competishun.ui.adapter.TeacherAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.CreateCartViewModel
import xyz.penpencil.competishun.ui.viewmodel.GetCourseByIDViewModel
import xyz.penpencil.competishun.ui.viewmodel.StudentCoursesViewModel
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import xyz.penpencil.competishun.utils.StudentCourseItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.ExploreCourse
import xyz.penpencil.competishun.databinding.FragmentExploreBinding
import xyz.penpencil.competishun.ui.adapter.ExploreCourseAdapter
import xyz.penpencil.competishun.ui.main.PdfViewActivity
import xyz.penpencil.competishun.ui.viewmodel.MyCoursesViewModel
import xyz.penpencil.competishun.ui.viewmodel.UserViewModel


@AndroidEntryPoint
class ExploreFragment : DrawerVisibility(), OurContentAdapter.OnItemClickListener,
    StudentCourseItemClickListener {

    private lateinit var binding: FragmentExploreBinding
    private var player: ExoPlayer? = null
    private val getCourseByIDViewModel: GetCourseByIDViewModel by viewModels()
    private lateinit var combinedTabItems: List<TabItem>
    private lateinit var limitedFaqItems: List<FAQItem>
    private lateinit var faqItems: List<FAQItem>
    private val createCartViewModel: CreateCartViewModel by viewModels()
    private val cartViewModel: CreateCartViewModel by viewModels()
    private val courseViewModel: StudentCoursesViewModel by viewModels()
    private val viewModel: MyCoursesViewModel by viewModels()
    private var showMoreOrLess = ObservableField("View More")
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    var isItemSize = ObservableField(true)
    private lateinit var courseId: String
    lateinit var folderlist: List<GetCourseByIdQuery.Folder>
    private lateinit var helperFunctions: HelperFunctions
    val lectureCounts = mutableMapOf<String, Int>()
    var firstInstallment: Int = 0
    var secondInstallment: Int = 0
    var ExploreCourseTags: MutableList<String> = mutableListOf()
    var bannerCourseTag: MutableList<String> = mutableListOf()
    var isVideoPlaying = false
    var installmentPrice1 = 0
    private var checkInstallOrNot = 0

    private var categoryName = ""
    private var courselreadyBuy = false
    var faqAdapter: FAQAdapter? = null

    var courseData: MyCoursesQuery.MyCourse? = null
    var ourContentAdapter: OurContentAdapter? = null

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreBinding.inflate(inflater, container, false).apply {
            this.courseByIDViewModel = this@ExploreFragment.getCourseByIDViewModel
            lifecycleOwner = viewLifecycleOwner

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        helperFunctions = HelperFunctions()
        combinedTabItems = listOf()

        binding.backIv.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        arguments?.let { bundle ->
            val tags = bundle.getStringArrayList("course_tags")
            val recommendCourseTags = bundle.getStringArrayList("recommendCourseTags")
            val bannerCourseTags = bundle.getStringArrayList("bannerCourseTag")

            Log.d("recommendCourseTags", recommendCourseTags.toString())
            Log.d("tags", tags.toString())
            Log.d("bannerCourseTags", bannerCourseTags.toString())

            when {
                // If recommendCourseTags is available and not empty, use it
                recommendCourseTags != null && recommendCourseTags.isNotEmpty() -> {
                    ExploreCourseTags.clear()
                    ExploreCourseTags.addAll(recommendCourseTags)
                    Log.e("courseTags", "Received Recommend Course Tags: $ExploreCourseTags")
                }

                // If recommendCourseTags is null or empty, use tags if available
                tags != null && tags.isNotEmpty() -> {
                    ExploreCourseTags.clear()
                    ExploreCourseTags.addAll(tags)
                    Log.e("courseTags", "Received Course Tags: $ExploreCourseTags")
                }

                // If both recommendCourseTags and tags are null or empty, use bannerCourseTags
                bannerCourseTags != null && bannerCourseTags.isNotEmpty() -> {
                    ExploreCourseTags.clear()
                    ExploreCourseTags.addAll(bannerCourseTags)
                    Log.e("courseTags", "Received Banner Course Tags: $ExploreCourseTags")
                }

                // Handle case where all tags are null or empty
                else -> {
                    Log.e("courseTags", "No valid course tags received.")
                }
            }
        }

        val tvAgreeTerms: TextView = view.findViewById(R.id.tvAgreeTerms)
        val tvCourseDescription: TextView = view.findViewById(R.id.tvCourseDescription)
        val nestedScrollViewMove: NestedScrollView = view.findViewById(R.id.nested_scroll_view)

        val termsText =
            "By proceeding with this purchase, you confirm that you have read and agreed to the Terms and Conditions, as well as the Refund Policy."
        val spannableString = SpannableString(termsText)

        val termsClickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                binding.tvCourseDescription.maxLines = Integer.MAX_VALUE
                scrollToHeading(tvCourseDescription, nestedScrollViewMove, "Terms and Conditions")
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true // Remove underline if you want
                ds.color = Color.BLUE       // Set color to blue
            }
        }

        val refundClickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                binding.tvCourseDescription.maxLines = Integer.MAX_VALUE
                scrollToHeading(tvCourseDescription, nestedScrollViewMove, "Refund Policy")
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true // Remove underline if you want
                ds.color = Color.BLUE       // Set color to blue
            }
        }

        val termsStart = termsText.indexOf("Terms and Conditions")
        val termsEnd = termsStart + "Terms and Conditions".length
        spannableString.setSpan(
            termsClickable,
            termsStart,
            termsEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val refundStart = termsText.indexOf("Refund Policy")
        val refundEnd = refundStart + "Refund Policy".length
        spannableString.setSpan(
            refundClickable,
            refundStart,
            refundEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvAgreeTerms.text = spannableString
        tvAgreeTerms.movementMethod = LinkMovementMethod.getInstance()

        val checkBox: CheckBox = view.findViewById(R.id.cbAgreeTerms)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            binding.clBuynow.isEnabled = isChecked

            if (isChecked) {
                binding.clBuynow.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue_3E3EF7
                    )
                )
            } else {
                binding.clBuynow.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey_button
                    )
                )
            }
        }

        getCourseTagsData()

        binding.clBuynow.setOnClickListener {
            Log.d("courselreadyBuy", courselreadyBuy.toString())
            Log.d("courseData", courseData.toString())
            if (courselreadyBuy && courseData != null) {
                val bundle = Bundle().apply {
                    val gson = Gson()
                    val course = courseData?.course
                    val folderJson = courseData?.progress?.subfolderDurations?.map {
                        it.folder
                    }
                    val progressPercentages = courseData?.progress?.subfolderDurations
                        ?.map { it.completionPercentage }

                    putString("folderJson", gson.toJson(folderJson))
                    putString("courseJson", gson.toJson(course))
                    putString("courseName", course?.name)
                    putString("courseId", course?.id)
                    putString("courseStart", course?.course_start_date.toString())
                    putString("courseEnd", course?.course_end_date.toString())
                    putString("completionPercentages", gson.toJson(progressPercentages))
                }

                Log.e("sdjfkjsdhfjsad", "onViewCreated: ")

                findNavController().navigate(R.id.ResumeCourseFragment, bundle)
            } else {
                createCart(createCartViewModel, "FullPayment")
            }
        }

        var lectureCount = arguments?.getString("LectureCount")
        courseId = arguments?.getString("course_id").toString()
        folderlist = emptyList()
        getAllLectureCount(courseId)
        initObserver()
        Log.d("courselreadyBuyinit", courselreadyBuy.toString())
        if (lectureCount.isNullOrEmpty()) {
            lectureCount = "0"
        }
        ourContentAdapter = OurContentAdapter(folderlist, isItemSize, this)
        binding.rvOurContent.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = ourContentAdapter
        }

        if (courseId.isEmpty()) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            getCourseByIDViewModel.fetchCourseById(courseId)
            isVideoPlaying = false
            getCourseByIDViewModel.courseByID.observe(viewLifecycleOwner, Observer { courses ->

                categoryName = courses?.category_name.toString()
                bannerCourseTag = courses?.course_tags as MutableList<String>
                checkInstallOrNot = courses?.with_installment_price ?: 0
                val imageUrl = courses?.video_thumbnail
                val videoUrl = courses?.orientation_video

                if (videoUrl.isNullOrEmpty()) {
                    binding.overviewButton.visibility = View.GONE
                    binding.ivBannerExplore.isClickable = false
                } else {
                    binding.ivBannerExplore.isClickable = true
                }

                Log.d("CourseVideoThumbnail", imageUrl ?: "No URL")
                Log.d("CourseOrientThumbnail", videoUrl ?: "No URL")

                Glide.with(requireContext())
                    .load(imageUrl)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .into(binding.ivBannerExplore)

                binding.ivBannerExplore.setOnClickListener {
                    if (!videoUrl.isNullOrEmpty()) {
                        binding.ivBannerExplore.visibility = View.GONE
                        binding.playerView.visibility = View.VISIBLE


                        initializePlayer(videoUrl)



                        player?.addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(state: Int) {
                                if (state == Player.STATE_ENDED) {
                                    binding.playerView.visibility = View.GONE
                                    binding.ivBannerExplore.visibility = View.VISIBLE
                                }
                            }
                        })
                    } else {
                        binding.ivBannerExplore.visibility = View.VISIBLE
                        binding.playerView.visibility = View.GONE
                        Log.d("CourseVideoError", "Video URL is empty")
                    }

                }

                binding.overviewButton.setOnClickListener {
                    if (!videoUrl.isNullOrEmpty()) {
                        if (isVideoPlaying) {
                            player?.pause()
                            isVideoPlaying = false
                        } else {
                            binding.ivBannerExplore.visibility = View.GONE
                            binding.playerView.visibility = View.VISIBLE

                            if (player == null) {
                                initializePlayer(videoUrl)
                            } else {
                                player?.play()
                            }
                            isVideoPlaying = true
                        }


                        player?.addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(state: Int) {
                                if (state == Player.STATE_ENDED) {
                                    binding.playerView.visibility = View.GONE
                                    binding.ivBannerExplore.visibility = View.VISIBLE
                                }
                            }
                        })
                    } else {
                        binding.ivBannerExplore.visibility = View.VISIBLE
                        binding.playerView.visibility = View.GONE
                        Log.d("CourseVideoError", "Video URL is empty")
                    }
                }




                installmentPrice1 = courses?.with_installment_price ?: 0
                Log.d("installmentPrice114", installmentPrice1.toString())
                firstInstallment = (installmentPrice1 * 0.6).toInt()
                secondInstallment = (installmentPrice1.minus(firstInstallment))

                if (firstInstallment <= 0) {
                    Log.d("checkInstallOrNot", checkInstallOrNot.toString())
                    binding.clInstallmentOptionView.visibility = View.GONE

                } else {
                    Log.d("checkInstallOrNot", checkInstallOrNot.toString())
                    binding.clInstallmentOptionView.visibility = View.VISIBLE
                    binding.clInstallmentOptionView.setOnClickListener {
                        showInstallmentDetailsBottomSheet(
                            firstInstallment,
                            secondInstallment
                        )
                    }
                }


                binding.progressBar.visibility = View.GONE
                binding.ExpireValidity.text =
                    "Validity: " + helperFunctions.formatCourseDate(courses?.course_validity_end_date.toString())

                if (courses.planner_pdf != null) {
                    binding.clGetPlanner.setOnClickListener {

                        Log.d("planner_pdf", courses.planner_pdf)
                        val intent = Intent(context, PdfViewActivity::class.java).apply {
                            putExtra("PDF_URL", courses.planner_pdf)
                            putExtra("PDF_TITLE", courses.name)
                        }
                        context?.startActivity(intent)

                    }
                    binding.downloadButton.setOnClickListener {

                        Log.d("planner_pdf", courses.planner_pdf)
                        helperFunctions.showDownloadDialog(
                            requireContext(),
                            courses.planner_pdf,
                            "Planner"
                        )

                    }
                } else {
//                    Toast.makeText(requireContext(), "", Toast.LENGTH_LONG).show()
                }

                if (courses != null) {
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

                    val courseFeaturesAdapter = CourseFeaturesAdapter(courseFItems)
                    binding.rvCourseFeatures.apply {
                        layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = courseFeaturesAdapter
                    }
                    folderlist = courses.folder ?: emptyList()
                    val sortedFolderList = folderlist.sortedByDescending {
                        it.name.startsWith("Class")
                    }
                    binding.tvOurContentNumber.text = folderlist.size.toString() + " Total"


                    binding.tvCourseName.text = courses.name
                    val categoryName = courses.category_name?.split(" ") ?: emptyList()
                    if (courses.discount == null) {
                        binding.dicountPricexp.text = "₹${courses.price}"
                        binding.orgPricexp.visibility = View.GONE
                    } else {
                        binding.orgPricexp.text = "₹" + courses.price.toString()
                        binding.dicountPricexp.text = "₹${courses.discount ?: 0}"
                    }
                    binding.tvStartDate.text =
                        "Starts On: " + helperFunctions.formatCourseDate(courses.course_start_date.toString())
                    binding.tvEndDate.text =
                        "Ends On: " + helperFunctions.formatCourseDate(courses.course_end_date.toString())

                    val freeCourse = courses.folder?.get(0)?.name?.split(" ")?.get(0)
                    Log.e("getFreeCourse", freeCourse.toString())

                    ourContentAdapter?.updateItems(folderlist)
                }
                faqItems = getFaqItemsByCategory(categoryName)
                limitedFaqItems = faqItems.take(4)
                faqAdapter?.updateAdapter(limitedFaqItems)
//                faqAdapter?.notifyDataSetChanged()
            })
        }

        binding.tvCourseDescription.viewTreeObserver.addOnGlobalLayoutListener {
            if (binding.tvCourseDescription.lineCount <= 2) {
                binding.tvReadMore.visibility = View.GONE
            } else {
                binding.tvReadMore.visibility = View.VISIBLE
            }
        }

        binding.tvReadMore.setOnClickListener {
            if (binding.tvCourseDescription.maxLines == 3) {
                binding.tvCourseDescription.maxLines = Integer.MAX_VALUE
                binding.tvCourseDescription.ellipsize = null
                binding.tvReadMore.text = "Read Less"
                binding.tvReadMore.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.arrow_up_explore, 0
                )
            } else {
                binding.tvCourseDescription.maxLines = 3
                binding.tvCourseDescription.ellipsize = android.text.TextUtils.TruncateAt.END
                binding.tvReadMore.text = "Read More"
                binding.tvReadMore.visibility = View.VISIBLE
                binding.tvReadMore.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.arrow_down, 0
                )
            }
        }

        binding.tvCoursePlannerDescription.viewTreeObserver.addOnGlobalLayoutListener {
            if (binding.tvCoursePlannerDescription.lineCount <= 2) {
                binding.tvPlannerReadMore.visibility = View.GONE
            } else {
                binding.tvPlannerReadMore.visibility = View.VISIBLE
            }
        }

        binding.tvPlannerReadMore.setOnClickListener {
            if (binding.tvCoursePlannerDescription.maxLines == 3) {
                binding.tvCoursePlannerDescription.maxLines = Integer.MAX_VALUE
                binding.tvCoursePlannerDescription.ellipsize = null
                binding.tvPlannerReadMore.text = "Read Less"
                binding.tvPlannerReadMore.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.arrow_up_explore, 0
                )
            } else {
                binding.tvCoursePlannerDescription.maxLines = 3
                binding.tvCoursePlannerDescription.ellipsize = android.text.TextUtils.TruncateAt.END
                binding.tvPlannerReadMore.text = "Read More"
                binding.tvPlannerReadMore.visibility = View.VISIBLE
                binding.tvPlannerReadMore.setCompoundDrawablesWithIntrinsicBounds(
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
            ourContentAdapter?.notifyDataSetChanged()
        }


        val teacherItems = listOf(
            TeacherItem(
                R.drawable.alok,
                "ALOK KUMAR",
                "CHEMISTRY (P/I)",
                "He has held senior faculty positions at many reputed IIT-JEE coaching institutions.\", \"Known for his organized teaching style, making Chemistry simple and interesting for students.\",\"Relates various Chemistry topics to practical applications, fostering deep interest in the subject.\",\"Believes Science and technology will play a pivotal role in India's development.\",\"Strives to motivate students to pursue careers in Science and technology."
            ),
            TeacherItem(
                R.drawable.neeraj,
                "NEERAJ SAINI",
                "CHEMISTRY (ORG)",
                "He has 14 years of experience teaching Organic Chemistry as a Senior Faculty at a reputed national institute.\", \"Known for his concise and simplified teaching style, helping students score well in Organic Chemistry.\",\"Has mentored many students who secured Top-100 AIRs in IIT-JEE (Main) and (Main+Advanced)."
            ),
            TeacherItem(
                R.drawable.mohit,
                "MOHIT TYAGI",
                "MATHEMATICS",
                "\"Mohit Tyagi Sir has over 22 years of experience in teaching Mathematics.\\\", \\\"He is renowned for creating a love for Mathematics among students.\\\",\\\"Previously served as the Head of the Maths Team (HOD) at a leading coaching institute in Kota.\\\",\\\"His YouTube channel is a source of inspiration for both students and faculty members.\""
            ),
            TeacherItem(
                R.drawable.amit,
                "AMIT BIJARNIA",
                "PHYSICS",
                "He is an enthusiastic Physics teacher, highly popular among JEE (Advanced)/IIT-JEE students.\", \"Known for helping students visualize problems and reach solutions quickly.\",\"Adored by students for his clear, engaging teaching style that fosters a love for Physics.\",\"Many of his students from Kota have achieved prestigious ranks in IIT-JEE (Advanced)."
            ),
        )
        val teacherAdapter = TeacherAdapter(teacherItems)
        binding.rvMeetTeachers.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = teacherAdapter
        }

        Log.d("categoryName", categoryName)

        when (categoryName) {
            "Full Year Course" -> {
                faqItems = listOf(
                    FAQItem(
                        "Will I get Physical Study Material ?",
                        "YES, you will get the Physical study material along with this purchase. We will dispatch your material and tracking id will be provide so that you can track the parcel. The Expected Delivery of the Study Material is within 10-12 Working Days from the date of admission."
                    ),
                    FAQItem(
                        "Will Classes be any Live Classes ?",
                        "NO, There will be only Recorded Scheduled Lectures will be provided. In week there will be LIVE INTERACTION Session for your guidance for 30 Minutes."
                    ),
                    FAQItem(
                        "Will Test be conducted in this course ?",
                        "YES, Test will be conducted on weekly basis as per test grid that will be provided to you along with the course. Test will be conducted on COMPETISHUN DIGITAL APP / WEBSITE and we will share the complete details in your Official Support Prior to your 1st test."
                    ),
                    FAQItem(
                        "Will Doubt clearing session will be conducted ?",
                        "YES, you can ask your doubts in your Telegram doubt groups tagging faculties and you will get a reply at the earliest. Also there will be zoom Live Session."
                    ),
                    FAQItem(
                        "How Do you contact Support Staff of Competishun ?",
                        "You can contact Support Staff at 8888-0000-21, 7410-900-901."
                    )
                )

            }

            "Test Series" -> {
                faqItems = listOf(
                    FAQItem(
                        "How many tests are included in the series?",
                        "The test series includes 50+ topic-wise and full-syllabus tests."
                    ),
                    FAQItem(
                        "Are solutions provided for the tests?",
                        "Yes, detailed Text and Video solutions and analysis are provided after each test."
                    ),
                    FAQItem(
                        "Can I access the tests offline?",
                        "Tests can be accessed only through the app / Website in CBT Mode"
                    ),
                    FAQItem(
                        "Is there a performance report after each test?",
                        "Yes, a detailed performance analysis is shared for improvement tracking."
                    ),
                )
            }

            else -> {
                faqItems = listOf(
                    FAQItem(
                        "Will I get Physical Study Material ?",
                        " NO, With this Course Purchase, you will get only Digital Study Material like DPP’s and their respective Text and Video Solution. Physical Study Material is not provided with this Short term Course."
                    ),
                    FAQItem(
                        "Will Classes be any Live Classes ?",
                        "NO, There will be only Recorded Scheduled Lectures will be provided. In week there will be LIVE INTERACTION Session for your guidance for 30 Minutes"
                    ),
                    FAQItem(
                        "Will Test be conducted in this course ?",
                        "YES, Test will be conducted on weekly basis as per test grid that will be provided to you along with the course. Test will be conducted on COMPETISHUN DIGITAL APP / WEBSITE and we will share the complete details in your Official Support Prior to your 1st test."
                    ),
                    FAQItem(
                        "Will Doubt clearing session will be conducted ?",
                        "YES, you can ask your doubts in your doubt groups tagging faculties and you will get a reply at the earliest. "
                    ),
                    FAQItem(
                        "How Do you contact Support Staff of Competishun ?",
                        "You can contact Support Staff at 8888-0000-21, 7410-900-901 "
                    ),
                    FAQItem(
                        "Will I complete my entire syllabus in this short time?",
                        "Yes, the course is designed for rapid coverage, focusing on high-weightage topics to maximize your score."
                    ),
                    FAQItem(
                        "Do I get direct access to top educators?",
                        "Yes, students can interact with expert faculty via live sessions and doubt-solving forums."
                    )
                )

            }
        }

        limitedFaqItems = faqItems.take(4)

        faqAdapter = FAQAdapter(limitedFaqItems)

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
            category_name = Optional.present("Complimentary Course"),
            course_class = Optional.present("12th"),
            exam_type = Optional.present(null),
            is_recommended = Optional.present(null)
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
                            live_date = course.live_date,
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
                            with_installment_price = course.with_installment_price,
                            course_end_date = course.course_end_date

                        )
                    } ?: emptyList()
                    binding.rvRelatedCourses.adapter =
                        CourseAdapter(courses, lectureCounts, this@ExploreFragment)
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


    private fun initObserver() {
        userViewModel.userDetails.observe(viewLifecycleOwner) { result ->
            if (result.isFailure) return@observe
            result.onSuccess {
                it.getMyDetails.courses?.map { data ->
                    if (courseId == data?.enrolledCourseId) {
                        courselreadyBuy = true
                        binding.clBuynow.isEnabled = true
                        binding.tvAgreeTerms.visibility = View.GONE
                        binding.cbAgreeTerms.visibility = View.GONE
                        binding.clBuynow.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.blue_3E3EF7
                            )
                        )
                        binding.clBuynow.text = "Start Now"
                        Log.e("purchasweDat", courselreadyBuy.toString())
                        ourContentAdapter?.updateContent(true)
                        ourContentAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }
        userViewModel.fetchUserDetails()

        viewModel.myCourses.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                if (data.myCourses.isNotEmpty()) {
                    data.myCourses.forEach { myCourse ->
                        if (myCourse.course.id == courseId) {
                            courseData = myCourse
                            Log.d("courseDataGet", courseData.toString())
                        }
                    }
                }
            }.onFailure {
                Log.e("MyCoursesFail", it.message.toString())
                Toast.makeText(
                    requireContext(),
                    "Failed to load courses: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.fetchMyCourses()

    }
    private fun scrollToHeading(textView: TextView, scrollView: NestedScrollView, heading: String) {
        val layout = textView.layout ?: return
        val headingIndex = textView.text.indexOf(heading)
        if (headingIndex != -1) {
            val lineNumber = layout.getLineForOffset(headingIndex)
            val yPosition = layout.getLineTop(lineNumber)
            scrollView.smoothScrollTo(0, textView.top + yPosition)
        }
    }


    private fun getCourseTagsData() {
        viewLifecycleOwner.lifecycleScope.launch {
            courseViewModel.courses.collect { result ->
                when {
                    result?.isSuccess == true -> {
                        val data = result.onSuccess {
                            it.getAllCourseForStudent.courses.map { course ->
                                binding.tvTag4.text = "Target ${course.target_year}"
                                if (ExploreCourseTags.isEmpty()) {
                                    ExploreCourseTags = bannerCourseTag
                                }
                                ExploreCourseTags.let { tags ->
                                    binding.tvTag1.apply {
                                        val tag1 = tags.getOrNull(0) ?: ""
                                        Log.d("ExploreCourseTagsIndex", tag1)
                                        text = tag1
                                        visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
                                    }
                                    binding.tvTag2.apply {
                                        val tag2 = tags.getOrNull(1) ?: ""
                                        Log.d("ExploreCourseTagsIndex", tag2)
                                        text = tag2
                                        visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
                                    }
                                    binding.tvTag3.apply {
                                        val tag3 = tags.getOrNull(2) ?: ""
                                        Log.d("ExploreCourseTagsIndex", tag3)
                                        text = tag3
                                        visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
                                    }
                                } ?: run {
                                    Log.d("ExploreCourseTagsIndex", "ExploreCourseTags is null")
                                    binding.tvTag1.visibility = View.GONE
                                    binding.tvTag2.visibility = View.GONE
                                    binding.tvTag3.visibility = View.GONE
                                }
                            }
                        }
                        Log.d("CourseTagsData", "Success: $data")
                    }

                    result?.isFailure == true -> {
                        val exception = result.exceptionOrNull()
                        Log.d("CourseTagsData", "Error: ${exception?.message}")
                    }

                    else -> {
                        Log.d("CourseTagsData", "No data available")
                    }
                }
            }
        }

        // Fetching the courses with desired filters
        val filters = FindAllCourseInputStudent(/* initialize with required data */)
        courseViewModel.fetchCourses(filters)
//        printf "Hello world, Deepak was here in this code!";
    }


    override fun onFirstItemClick(
        folderId: String,
        folderName: String,
        free: Boolean,
        isPurchased: Boolean
    ) {
        val bundle = Bundle().apply {
            putString("folderId", folderId)
            putString("folderName", folderName)
            putBoolean("free", free)
            putBoolean("isPurchased", isPurchased)
        }
        findNavController().navigate(R.id.action_exploreFragment_to_demoFreeFragment, bundle)
    }

    private fun initializePlayer(videoUrl: String) {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player

        val mediaItem = MediaItem.fromUri(videoUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()

        // Add a listener to handle playback errors
        player?.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e("VideoPlaybackError", "Error during playback: ${error.message}")
                // Handle the error (e.g., show a message to the user, revert UI)
                binding.ivBannerExplore.visibility = View.VISIBLE
                binding.playerView.visibility = View.GONE
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    binding.playerView.visibility = View.GONE
                    binding.ivBannerExplore.visibility = View.VISIBLE
                }
            }
        })
    }


    fun formatFeatureText(feature: String): String {
        return feature
            .split("_")
            .joinToString(" ") { it.toLowerCase().capitalize() }
    }

    override fun onOtherItemClick(folderId: String, folderName: String, isPurchased: Boolean) {
        val bundle = Bundle().apply {
            putString("folderId", folderId)
            putString("folderName", folderName)
            putBoolean("isPurchased", isPurchased)
        }
        findNavController().navigate(R.id.action_exploreFragment_to_demoFreeFragment, bundle)
    }

    private fun navigateToFaqFragment() {
        val bundle = Bundle().apply {
            putParcelableArrayList("faq_items", ArrayList(faqItems))
        }
        findNavController().navigate(R.id.action_exploreFragment_to_allFaqFragment, bundle)
    }

    private fun releasePlayer() {
        player?.release()
        player = null
        isVideoPlaying = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }


    fun getFaqItemsByCategory(categoryName: String): List<FAQItem> {
        return when (categoryName) {
            "Full Year Course" -> {
                listOf(
                    FAQItem(
                        "Will I get Physical Study Material ?",
                        "YES, you will get the Physical study material along with this purchase. We will dispatch your material and tracking id will be provide so that you can track the parcel. The Expected Delivery of the Study Material is within 10-12 Working Days from the date of admission."
                    ),
                    FAQItem(
                        "Will Classes be any Live Classes ?",
                        "NO, There will be only Recorded Scheduled Lectures will be provided. In week there will be LIVE INTERACTION Session for your guidance for 30 Minutes."
                    ),
                    FAQItem(
                        "Will Test be conducted in this course ?",
                        "YES, Test will be conducted on weekly basis as per test grid that will be provided to you along with the course. Test will be conducted on COMPETISHUN DIGITAL APP / WEBSITE and we will share the complete details in your Official Support Prior to your 1st test."
                    ),
                    FAQItem(
                        "Will Doubt clearing session will be conducted ?",
                        "YES, you can ask your doubts in your Telegram doubt groups tagging faculties and you will get a reply at the earliest. Also there will be zoom Live Session."
                    ),
                    FAQItem(
                        "How Do you contact Support Staff of Competishun ?",
                        "You can contact Support Staff at 8888-0000-21, 7410-900-901."
                    )
                )
            }

            "Test Series" -> {
                listOf(
                    FAQItem(
                        "How many tests are included in the series?",
                        "The test series includes 50+ topic-wise and full-syllabus tests."
                    ),
                    FAQItem(
                        "Are solutions provided for the tests?",
                        "Yes, detailed Text and Video solutions and analysis are provided after each test."
                    ),
                    FAQItem(
                        "Can I access the tests offline?",
                        "Tests can be accessed only through the app / Website in CBT Mode."
                    ),
                    FAQItem(
                        "Is there a performance report after each test?",
                        "Yes, a detailed performance analysis is shared for improvement tracking."
                    )
                )
            }

            else -> {
                listOf(
                    FAQItem(
                        "Will I get Physical Study Material ?",
                        "NO, With this Course Purchase, you will get only Digital Study Material like DPP’s and their respective Text and Video Solution. Physical Study Material is not provided with this Short term Course."
                    ),
                    FAQItem(
                        "Will Classes be any Live Classes ?",
                        "NO, There will be only Recorded Scheduled Lectures will be provided. In week there will be LIVE INTERACTION Session for your guidance for 30 Minutes."
                    ),
                    FAQItem(
                        "Will Test be conducted in this course ?",
                        "YES, Test will be conducted on weekly basis as per test grid that will be provided to you along with the course. Test will be conducted on COMPETISHUN DIGITAL APP / WEBSITE and we will share the complete details in your Official Support Prior to your 1st test."
                    ),
                    FAQItem(
                        "Will Doubt clearing session will be conducted ?",
                        "YES, you can ask your doubts in your doubt groups tagging faculties and you will get a reply at the earliest."
                    ),
                    FAQItem(
                        "How Do you contact Support Staff of Competishun ?",
                        "You can contact Support Staff at 8888-0000-21, 7410-900-901."
                    ),
                    FAQItem(
                        "Will I complete my entire syllabus in this short time?",
                        "Yes, the course is designed for rapid coverage, focusing on high-weightage topics to maximize your score."
                    ),
                    FAQItem(
                        "Do I get direct access to top educators?",
                        "Yes, students can interact with expert faculty via live sessions and doubt-solving forums."
                    )
                )
            }
        }
    }


    private fun mapFolderToOurContentItem(folder: GetCourseByIdQuery.Folder): OurContentItem {
        val isFreeCourse = folder.name.split(" ")[0].equals("Class", ignoreCase = true)
        val drawableRes = if (isFreeCourse) R.drawable.group_1272628769 else R.drawable.lock
        Log.e("getFolderID ${folder.id}", "courseID" + folder.course_id)
        return OurContentItem.OtherItem(
            OtherContentItem(
                R.drawable.frame_1707480918,
                folder.name,
                drawableRes
            )
        )
    }

    fun createCart(createCartViewModel: CreateCartViewModel, paymentType: String) {


        val cartItems = listOf(
            CreateCartItemDto(
                entity_id = courseId,
                entity_type = EntityType.COURSE, quantity = 1
            )
        ) // Replace with actual cart items data
        val bundle = Bundle().apply {
            putString("paymentType", paymentType)
        }
        // Call the API to create cart items
        createCartViewModel.createCartItems(sharedPreferencesManager.userId.toString(), cartItems)

        // Observe the result and navigate based on success or failure
        createCartViewModel.cartItemsResult.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess {

                findNavController().navigate(R.id.myCartFragment, bundle)
            }.onFailure { exception ->
                Log.e("createCart", exception.message.toString())
                val errorMessage = exception.message.toString()
                if (errorMessage.startsWith("Duplicate entry")) {
                    findNavController().navigate(R.id.myCartFragment)
                }
            }
        })

    }

    override fun onCourseItemClicked(course: AllCourseForStudentQuery.Course, bundle: Bundle) {
        val bundle = Bundle().apply {
            putString("course_id", course.id)
        }
        findNavController().navigate(R.id.action_coursesFragment_to_ExploreFragment, bundle)
    }

    private fun showInstallmentDetailsBottomSheet(firstInstallment: Int, secondInstallment: Int) {
        val bottomSheet = InstallmentDetailsBottomSheet().apply {
            setInstallmentData(firstInstallment, secondInstallment)
            setOnBuyNowClickListener(object : InstallmentDetailsBottomSheet.OnBuyNowClickListener {
                override fun onBuyNowClicked(totalAmount: Int) {
                    createCart(cartViewModel, "Installment")
//                    Toast.makeText(context, "Buy Now clicked with total: ₹$totalAmount", Toast.LENGTH_SHORT).show()
                }
            })
        }
        bottomSheet.show(parentFragmentManager, "InstallmentDetailsBottomSheet")
    }

    fun getAllLectureCount(courseId: String) {

        courseViewModel.fetchLectures(courseId)
        Log.e("getcourseIds", courseId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                courseViewModel.lectures.collect { result ->
                    result?.onSuccess { lecture ->
                        val count = lecture.getAllCourseLecturesCount.lecture_count.toInt()
                        binding.tvLectureNo.text = "Lectures: $count"
                        Log.e("lectureCount", count.toString())
                        //  callback(courseId, count)
                    }?.onFailure { exception ->
                        Log.e("LectureException", exception.toString())
                    }
                }
            }
        }
    }


}
