package xyz.penpencil.competishun.ui.fragment

import android.app.Activity
import android.content.Intent
import xyz.penpencil.competishun.utils.HorizontalCalendarSetUp
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.student.competishun.curator.FindAllCourseFolderContentByScheduleTimeQuery
import xyz.penpencil.competishun.data.model.CalendarDate
import xyz.penpencil.competishun.data.model.ScheduleData
import xyz.penpencil.competishun.ui.adapter.ScheduleAdapter
import xyz.penpencil.competishun.ui.main.HomeActivity
import xyz.penpencil.competishun.ui.viewmodel.MyCoursesViewModel
import xyz.penpencil.competishun.ui.viewmodel.VideourlViewModel
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.ToolbarCustomizationListener
import dagger.hilt.android.AndroidEntryPoint
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.FragmentScheduleBinding
import xyz.penpencil.competishun.ui.main.PdfViewActivity
import xyz.penpencil.competishun.utils.setLightStatusBars
import xyz.penpencil.competishun.utils.timeStatus
import xyz.penpencil.competishun.utils.toIstZonedDateTime
import xyz.penpencil.competishun.utils.utcToIst
import xyz.penpencil.competishun.utils.utcToIstYYYYMMDD
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale
import java.time.ZonedDateTime
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.TimeZone

@AndroidEntryPoint
class ScheduleFragment : DrawerVisibility(), ToolbarCustomizationListener {

    private val binding by lazy {
        FragmentScheduleBinding.inflate(layoutInflater)
    }
    private var foundMatchingDate = false
    private val videoUrlViewModel: VideourlViewModel by viewModels()
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var helperFunctions: HelperFunctions
    private val myCourseViewModel: MyCoursesViewModel by viewModels()
    private var matchingPosition: Int? = null
    var courseName  =  ""
    var courseId  =  ""
    private var courseStart  =  ""
    private var courseEnd  =  ""
    var courses =  ""
    private var selectedDate : ZonedDateTime = ZonedDateTime.now()

    private var listData: List<FindAllCourseFolderContentByScheduleTimeQuery.FindAllCourseFolderContentByScheduleTime> = mutableListOf()

    private var hasScheduleList = mutableListOf<String>()

    private val calendarSetUp : HorizontalCalendarSetUp by lazy { HorizontalCalendarSetUp() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private fun setupCalendar(scheduleTime:String) {


        calendarSetUp.setUpCalendarAdapter(
            binding.rvCalenderDates,
            requireContext(),
            onDateSelected = { calendarDate ->
                calendarDate.zonedDateTime?.let {
                    selectedDate = it
                }
                setupRecyclerView()
            }
            ,hasScheduleList
        )

        val month = convertIST(selectedDate.toString()).month
        val year = convertIST(selectedDate.toString()).year
        binding.tvCalenderCurrentMonth.text = String.format(getString(R.string.current_month_year), month, year)

        calendarSetUp.setUpCalendarPrevNextClickListener(
            binding.rvCalenderDates,
            binding.arrowRightCalender,
            binding.arrowLeftCalender,
            requireContext(),
            { newMonth ->
                binding.tvCalenderCurrentMonth.text = newMonth.uppercase()
                courseStart = getStartOfMonth(newMonth)
                courseEnd = getEndOfMonth(newMonth)
                selectedDate = courseStart.utcToIst().toIstZonedDateTime()
                fetchData()
            },
            { calendarDate ->
                scrollToDate(calendarDate)
            }
        )

        calendarSetUp.scrollToSpecificDate(binding.rvCalenderDates, convertIST(scheduleTime))
    }

    private fun getStartOfMonth(monthYear: String): String {
        val yearMonth = YearMonth.parse(monthYear, DateTimeFormatter.ofPattern("MMMM yyyy"))
        val startOfMonth = yearMonth.atDay(1).atStartOfDay().atOffset(ZoneOffset.UTC)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        return startOfMonth.format(formatter)
    }

    private fun getEndOfMonth(monthYear: String): String {
        val yearMonth = YearMonth.parse(monthYear, DateTimeFormatter.ofPattern("MMMM yyyy"))
        val endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59, 999000000).atOffset(ZoneOffset.UTC)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        return endOfMonth.format(formatter)
    }

    private fun setUpScheduleAvailable(hasScheduleList: MutableList<String>) {
        calendarSetUp.setUpScheduleAvailable(binding.rvCalenderDates, hasScheduleList,requireContext()) { calendarDate ->
              scrollToDate(calendarDate)
        }
    }

    private fun setupRecyclerView() {
        val contentList = listData
        val filteredContentList = contentList.filter {
            val scheduledTime = it.content.scheduled_time.toString().utcToIst().toIstZonedDateTime()
            scheduledTime.toLocalDate() == selectedDate.toLocalDate()
        }

        val scheduleDataList = filteredContentList.groupBy {
            val scheduledTime = it.content.scheduled_time.toString().utcToIst().toIstZonedDateTime()
            scheduledTime.dayOfWeek.toString().take(3) to scheduledTime.dayOfMonth.toString()
        }.map { (dateInfo, groupedContentList) ->
            val (dayOfWeek, dayOfMonth) = dateInfo
            ScheduleData(
                dayOfWeek,
                dayOfMonth,
                duration = 0,
                groupedContentList.map { content ->
                    ScheduleData.InnerScheduleItem(
                        content.folderPath ?: "",
                        content.content.file_name,
                        lecture_start_time = formatTime(convertIST(content.content.scheduled_time.toString())),
                        lecture_end_time = convertLastDuration(formatTime(convertIST(content.content.scheduled_time.toString())), content.content.video_duration?.toLong() ?: 0),
                        content.content.file_url.toString(),
                        content.content.file_type.name,
                        content.content.id,
                        content.content.scheduled_time.toString(),
                        completedDuration = content.studentTrack?.completed_duration ?: 0,
                        statusTime = content.content.scheduled_time.toString().timeStatus(content.content.video_duration ?: 0)
                    )
                }
            )
        }

        scheduleAdapter = ScheduleAdapter(scheduleDataList, requireContext(), this)
        binding.rvCalenderSchedule.adapter = scheduleAdapter
        binding.rvCalenderSchedule.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        if (scheduleDataList.isEmpty()){
            binding.clEmptySchedule.visibility = View.VISIBLE
            binding.rvCalenderSchedule.visibility = View.GONE
        }else {
            binding.clEmptySchedule.visibility = View.GONE
            binding.rvCalenderSchedule.visibility = View.VISIBLE
        }
    }


    private fun findAllCourseFolderContentByScheduleTimeQuery(){
        myCourseViewModel.courseFolderContent.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                foundMatchingDate = false
                hasScheduleList.clear()

                data.findAllCourseFolderContentByScheduleTime.forEachIndexed { index, scheduleContent ->
                    scheduleContent.content.scheduled_time?.let {
                        val currentDate = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        val regex = Regex("""(\d{4}-\d{2}-\d{2})""")
                        val extractedDate = regex.find(it.toString().utcToIstYYYYMMDD())?.value ?: "Invalid date"

                        if (extractedDate != "Invalid date") {
                            val dateParts = extractedDate.split("-")
                            val extractedDay = dateParts[2]

                            try {
                                val targetDate = LocalDate.of(dateParts[0].toInt(), dateParts[1].toInt(), dateParts[2].toInt())
                                val lDate = LocalDate.now()
                                if (lDate == targetDate) {
                                    selectedDate = ZonedDateTime.now()
                                }
                            } catch (e: Exception) {
                                println(e.message)
                            }

                            if (!hasScheduleList.contains(extractedDay)) {
                                hasScheduleList.add(extractedDay)
                            }

                            if (!foundMatchingDate) {
                                setupCalendar(it.toString())
                            }

                            if (currentDate == extractedDate) {
                                matchingPosition = index
                                foundMatchingDate = true
                                binding.rvCalenderSchedule.scrollToPosition(index - 2)
                                setupCalendar(it.toString())
                                return@forEachIndexed
                            }
                        }
                    }
                    val istDate = convertIST(selectedDate.toString())
                    val month = istDate.month
                    val year = istDate.year
                    binding.tvCalenderCurrentMonth.text = String.format(getString(R.string.current_month_year), month, year)
                }

                Log.e("dnmsbadbnsabd",
                    "findAllCourseFolderContentByScheduleTimeQuery: $hasScheduleList"
                )
                if (data.findAllCourseFolderContentByScheduleTime.isEmpty()){
                    binding.clEmptySchedule.visibility = View.VISIBLE
                    binding.rvCalenderSchedule.visibility = View.GONE
//                    setupCalendar(courseStart)
                    listData = mutableListOf()
                }else{
                    binding.clEmptySchedule.visibility = View.GONE
                    binding.rvCalenderSchedule.visibility = View.VISIBLE
                    listData = data.findAllCourseFolderContentByScheduleTime
                    setupRecyclerView()
                }
                setUpScheduleAvailable(hasScheduleList)
            }.onFailure { exception ->
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun convertIST(dateString: String): ZonedDateTime {
        val zonedDateTime = ZonedDateTime.parse(dateString) // Adjust this according to your input format
        return zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
    }

    private fun convertLastDuration(timeString: String, secondsToAdd: Long): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        val localTime = LocalTime.parse(timeString, formatter)
        val updatedLocalTime = localTime.plusSeconds(secondsToAdd)
        return updatedLocalTime.format(formatter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
                bottomNavigationView.selectedItemId = R.id.myCourse
            }
        })

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
        helperFunctions = HelperFunctions()
        courseName  =  arguments?.getString("courseName")?:""
        courseId  =  arguments?.getString("courseId")?:""
        val date = getStartAndEndOfMonth()
        courseStart  =  date.first
        courseEnd  =  date.second
        courses =  arguments?.getString("courses")?:""

       binding.backIconSchedule.setOnClickListener {
            it.findNavController().popBackStack()
        }
        binding.clEmptySchedule.visibility = View.VISIBLE
        findAllCourseFolderContentByScheduleTimeQuery()
        fetchData()
    }

    private fun fetchData(){
        val starts = courseStart.toFormattedDate()?:return
        val ends = courseEnd.toFormattedDate()?:return
        myCourseViewModel.getCourseFolderContent(starts,ends, courseId)
    }

    private fun String.toFormattedDate(): String? {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val outputFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")

        return try {
            val dateTime = LocalDateTime.parse(this, inputFormatter)
            dateTime.format(outputFormatter)
        } catch (e: Exception) {
            null
        }
    }

    private fun getStartAndEndOfMonth(): Pair<String, String> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = dateFormat.format(startCalendar.time)
        val endCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endOfDay = dateFormat.format(endCalendar.time)
        return Pair(startOfDay, endOfDay)
    }

    private fun scrollToDate(calendarDate: CalendarDate) {
        val position = scheduleAdapter.findPositionByDate(calendarDate.date)
        if (position != -1) {
            val layoutManager = binding.rvCalenderSchedule.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }
    
    private fun formatTime(zonedDateTime: ZonedDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return zonedDateTime.format(formatter)
    }

    override fun onCustomizeToolbar(fileurl: String, fileType: String,contentId:String) {
        if (fileType == "VIDEO"){
            videoUrlApi(videoUrlViewModel,contentId,"About this Course")
        }else if (fileType == "PDF"){
            val intent = Intent(context, PdfViewActivity::class.java).apply {
                putExtra("PDF_URL", fileurl)
            }
            context?.startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        setStatusBarGradiant(requireActivity())
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window?.let {
            it.statusBarColor = ContextCompat.getColor(requireContext(), R.color._white_F6F6FF)
            it.setLightStatusBars(false)
        }
    }

    private fun videoUrlApi(viewModel: VideourlViewModel, folderContentId: String, name: String) {
        viewModel.fetchVideoStreamUrl(folderContentId, "480")
        viewModel.videoStreamUrl.observe(viewLifecycleOwner) { signedUrl ->
            if (signedUrl != null) {
                val bundle = Bundle().apply {
                    putString("url", signedUrl)
                    putString("url_name", name)
                    putString("ContentId", folderContentId)
                    putStringArrayList("folderContentIds", arrayListOf())
                    putStringArrayList("folderContentNames", arrayListOf())
                }
                findNavController().navigate(R.id.mediaFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        requireActivity().window.setBackgroundDrawable(null)
    }

    private fun setStatusBarGradiant(activity: Activity) {
        val window: Window = activity.window
        val background = ContextCompat.getDrawable(activity, R.drawable.gradiant_bg_schedule)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity,android.R.color.transparent)
        window.setBackgroundDrawable(background)
    }
}


