package xyz.penpencil.competishun.ui.fragment

import android.content.Intent
import xyz.penpencil.competishun.utils.HorizontalCalendarSetUp
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale
import java.time.ZonedDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class ScheduleFragment : DrawerVisibility(), ToolbarCustomizationListener {

    private val binding by lazy {
        FragmentScheduleBinding.inflate(layoutInflater)
    }
    var foundMatchingDate = false
    private val videourlViewModel: VideourlViewModel by viewModels()
    private lateinit var calendarSetUp: HorizontalCalendarSetUp
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var helperFunctions: HelperFunctions
    private val myCourseViewModel: MyCoursesViewModel by viewModels()
    lateinit var scheduleData:ZonedDateTime
    var matchingPosition: Int? = null
    var courseName  =  ""
    var courseId  =  ""
    var courseStart  =  ""
    var courseEnd  =  ""
    var courses =  ""

    var hasScheduleList= mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private fun setupCalendar(scheduleTime:String) {
        calendarSetUp = HorizontalCalendarSetUp()

        calendarSetUp.setUpCalendarAdapter(
            binding.rvCalenderDates,
            requireContext(),
            onDateSelected = { calendarDate ->
                scrollToDate(calendarDate)
            }
            ,hasScheduleList
        )
        //Log.e("schedule57 $scheduleTime", convertIST(scheduleTime).month.toString() )
        binding.tvCalenderCurrentMonth.text = "${convertIST(scheduleData.toString()).month} ${convertIST(scheduleData.toString()).year} "

        calendarSetUp.setUpCalendarPrevNextClickListener(
            binding.rvCalenderDates,
            binding.arrowRightCalender,
            binding.arrowLeftCalender,
            requireContext(),
            { newMonth ->
              //  binding.tvCalenderCurrentMonth.text = newMonth
            },
            { calendarDate ->
              //  scrollToDate(calendarDate)
            }
        )

        val today = LocalDate.now() // Get current date without time

        calendarSetUp.scrollToSpecificDate(binding.rvCalenderDates, convertIST(scheduleTime))

      //  calendarSetUp.scrollToSpecificDate(binding.rvCalenderDates, convertIST(scheduleTime))
    }

    private fun setUpScheduleAvailable(hasScheduleList: MutableList<String>) {
        calendarSetUp.setUpScheduleAvailable(binding.rvCalenderDates, hasScheduleList,requireContext()) { calendarDate ->
              scrollToDate(calendarDate)
        }
    }

    private fun setupRecyclerView(contentList: List<FindAllCourseFolderContentByScheduleTimeQuery.FindAllCourseFolderContentByScheduleTime>) {
    Log.e("foldevontent" ,contentList.toString())
        val currentDate = LocalDate.now()
        val day: Int = currentDate.dayOfMonth
        val month: Int = currentDate.monthValue
        val year: Int = currentDate.year
        val d = if (day<10) "0$day" else day.toString()
        val m = if (month<10) "0$month" else month.toString()
        val courseDate = CalendarDate(d,"$day","",null)

        val scheduleDataList = contentList.groupBy {
            val scheduledTime = convertIST(it.content.scheduled_time.toString())
            scheduledTime.dayOfWeek.toString().take(3) to scheduledTime.dayOfMonth.toString()
        }.map { (dateInfo, groupedContentList) ->
            val (dayOfWeek, dayOfMonth) = dateInfo
            ScheduleData(
                dayOfWeek,
                dayOfMonth,
                duration = 0,
                groupedContentList.map { content ->
                    ScheduleData.InnerScheduleItem(
                        content.folderPath?:"",
                        content.content.file_name,
                        formatTime(convertIST(content.content.scheduled_time.toString())),
                        convertLastDuration(formatTime(convertIST(content.content.scheduled_time.toString())),content.content.video_duration?.toLong()?:0),
                        content.content.file_url.toString(),
                        content.content.file_type.name,
                        content.content.id,
                        content.content.scheduled_time.toString(),
                        completedDuration = content.studentTrack?.completed_duration ?:0
                    )
                }
            )
        }


        scheduleAdapter = ScheduleAdapter(scheduleDataList, requireContext(),this)
        binding.rvCalenderSchedule.adapter = scheduleAdapter
        binding.rvCalenderSchedule.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        Handler(Looper.getMainLooper()).postDelayed({
            scrollToDate(courseDate)
        }, 2000)

        scheduleDataList.forEach { scheduleData ->
            Log.e("ScheduleDatakaj", scheduleData.toString())
        }
    }

    fun FindAllCourseFolderContentByScheduleTimeQuery(){
        myCourseViewModel.courseFolderContent.observe(viewLifecycleOwner) { result ->
            Log.e("getdatafschedr",result.toString())
            result.onSuccess { data ->
                Log.e("getdatafolder",data.toString())
                if (data.findAllCourseFolderContentByScheduleTime.isEmpty()){
                   binding.clEmptySchedule.visibility = View.VISIBLE
                }else{
                    binding.clEmptySchedule.visibility = View.GONE
                setupRecyclerView(data.findAllCourseFolderContentByScheduleTime)
                }
                Log.e("timesize",data.findAllCourseFolderContentByScheduleTime.size.toString())
                data.findAllCourseFolderContentByScheduleTime.forEachIndexed { index, scheduleContent ->
                    Log.e("timea $index", scheduleContent.content.scheduled_time.toString())
                }

                data.findAllCourseFolderContentByScheduleTime.forEachIndexed {index, schedulecontent->
                // Log.e("timea",schedulecontent.s.toString())
                 schedulecontent.content.scheduled_time.let {
                    Log.e("scheduletimess", convertIST(it.toString()).toString())
                     val currentDate = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                     val regex = Regex("""(\d{4}-\d{2}-\d{2})""")
                     val matchResult = regex.find(it.toString())
                     val extractedDate = matchResult?.value ?: "Invalid date"
                     val date=extractedDate.split("-")[2]
                     Log.e("scheduletimecu ","$extractedDate $it")

                     Log.e("aman",date)

                     if(!hasScheduleList.contains(date))
                     {
                         hasScheduleList.add(date)
                     }

                     if (!foundMatchingDate){
                         setupCalendar(it.toString())
                     }
                     if (currentDate == extractedDate){
                         matchingPosition = index
                         Log.e("datematching $matchingPosition",it.toString())
                         foundMatchingDate = true
                         binding.rvCalenderSchedule.scrollToPosition(index-2)
                         setupCalendar(it.toString())
                         return@forEachIndexed
                     }


//                     if (currentDate != it)
//                         setupCalendar(it.toString()) else setupCalendar(currentDate)

                 }
                    Log.e("schedule57 $scheduleData", convertIST(scheduleData.toString()).toString())
                    binding.tvCalenderCurrentMonth.text = "${convertIST(scheduleData.toString()).month} ${convertIST(scheduleData.toString()).year}"
                }

                setUpScheduleAvailable(hasScheduleList)
                Log.e("hasScheduleList",hasScheduleList.toString())

            }.onFailure { exception ->
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
      //  myCourseViewModel.getCourseFolderContent("08-27-2024", "10-30-2025", "31296a0b-6dea-42e5-b273-668744bf34a4")
    }

    fun convertIST(dateString: String): ZonedDateTime {
        val zonedDateTime = ZonedDateTime.parse(dateString) // Adjust this according to your input format
        return zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
    }

    fun convertLastDuration(timeString: String, secondsToAdd: Long): String {
        Log.e("getSWtring",timeString)

        // Define the formatter for formatting the output
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")

        // Parse the input time
        val localTime = LocalTime.parse(timeString, formatter)

        // Add the specified number of seconds
        val updatedLocalTime = localTime.plusSeconds(secondsToAdd)

        // Return the updated time in the same format
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

        calendarSetUp = HorizontalCalendarSetUp()

        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
        helperFunctions = HelperFunctions()
        courseName  =  arguments?.getString("courseName")?:""
        courseId  =  arguments?.getString("courseId")?:""
        courseStart  =  arguments?.getString("courseStart")?:""
        courseEnd  =  arguments?.getString("courseEnd")?:""
        courses =  arguments?.getString("courses")?:""

        scheduleData = ZonedDateTime.now()
        binding.backIconSchedule.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.clEmptySchedule.visibility = View.VISIBLE
        FindAllCourseFolderContentByScheduleTimeQuery()
        fetchData()
    }

    private fun fetchData(){
        var start = helperFunctions.formatCourseDate(courseStart)
        var end = helperFunctions.formatCourseDate(courseEnd)
        var starts = getDateBeforeDays(dateFormate(start),7)
        var ends = getDateAfterDays(dateFormate(end),7)
        myCourseViewModel.getCourseFolderContent(starts,ends, courseId)
    }

    fun dateFormate(inputDate:String):String{

        val inputFormat = SimpleDateFormat("dd MMM, yy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())

        return try {
            val date = inputFormat.parse(inputDate) // Parse the input date
            val formattedDate = outputFormat.format(date) // Format it into the desired format
           formattedDate
        } catch (e: Exception) {
            e.printStackTrace()
            "-"// Handle any parsing errors
        }
    }

    
    fun convertToIST(time: String): ISTTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val utcTime = ZonedDateTime.parse(time, formatter)

        val istTime = utcTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"))

        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a")
        val formattedTime = istTime.format(timeFormatter)

        return ISTTime(istTime, formattedTime)
    }
    data class ISTTime(
        val zonedDateTime: ZonedDateTime,
        val formattedTime: String
    )


    private fun scrollToDate(calendarDate: CalendarDate) {
        val position = scheduleAdapter.findPositionByDate(calendarDate.date)
        if (position != -1) {
            val layoutManager = binding.rvCalenderSchedule.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(position, 0)
        }
    }

    
    fun convertCalender(scheduledTime: Any): CalendarDate {
        // Parse the scheduledTime to a ZonedDateTime object
        val zonedDateTime = ZonedDateTime.parse(scheduledTime.toString())

        // Format the date and day
        val dateFormat = DateTimeFormatter.ofPattern("dd", Locale.getDefault())
        val dayFormat = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
        val timeFormat = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

        val date = zonedDateTime.format(dateFormat)
        val day = zonedDateTime.format(dayFormat)
        val time = zonedDateTime.format(timeFormat)

        // Return a CalendarDate object
        return CalendarDate(date, day, time, zonedDateTime)
    }
    
    private fun formatTime(zonedDateTime: ZonedDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return zonedDateTime.format(formatter)
    }

    fun getDateBeforeDays(startDate: String, daysBefore: Int): String {
        Log.e("starteddate",startDate.toString())
        val inputFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())


        return try {
            val parsedDate = inputFormat.parse(startDate) ?: return "-"
            val calendar = Calendar.getInstance().apply {
                time = parsedDate
                add(Calendar.DAY_OF_MONTH, -daysBefore) // Subtract 7 days
            }
            outputFormat.format(calendar.time) // Return the new date formatted
        } catch (e: Exception) {
            e.printStackTrace()
            "-"
        }
    }

    private fun getDateAfterDays(endDate: String, daysAfter: Int): String {
        Log.e("startendd",endDate.toString())
        val inputFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())


        return try {
            val parsedDate = inputFormat.parse(endDate) ?: return "-"
            val calendar = Calendar.getInstance().apply {
                time = parsedDate
                add(Calendar.DAY_OF_MONTH, daysAfter) // Add 7 days
            }
            outputFormat.format(calendar.time) // Return the new date formatted
        } catch (e: Exception) {
            e.printStackTrace()
            "-"
        }
    }

    override fun onCustomizeToolbar(fileurl: String, fileType: String,contentId:String) {
        Log.e("fileuodld",fileurl.toString() + fileType.toString())
        if (fileType == "VIDEO"){
            Log.e("fileuodldd",fileType.toString())
            videoUrlApi(videourlViewModel,contentId,"About this Course")
        }else if (fileType == "PDF"){
            val intent = Intent(context, PdfViewActivity::class.java).apply {
                putExtra("PDF_URL", fileurl)
            }
            context?.startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        requireActivity().window?.let {
            it.statusBarColor = ContextCompat.getColor(requireContext(), R.color._e25b49)
            it.setLightStatusBars(true)
        }
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
         Log.e("foldfdfd",folderContentId)
        viewModel.videoStreamUrl.observe(viewLifecycleOwner) { signedUrl ->
            Log.d("Videourl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                val bundle = Bundle().apply {
                    putString("url", signedUrl)
                    putString("url_name", name)
                    putString("ContentId", folderContentId)
                    putStringArrayList("folderContentIds", arrayListOf())
                    putStringArrayList("folderContentNames", arrayListOf())
                }
                findNavController().navigate(R.id.mediaFragment, bundle)

            } else {
                // Handle error or null URL
            }
        }
    }


}


