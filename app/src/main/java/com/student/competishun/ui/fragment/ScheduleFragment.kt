package com.student.competishun.ui.fragment

import android.content.Intent
import com.student.competishun.utils.HorizontalCalendarSetUp
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.student.competishun.R
import com.student.competishun.curator.FindAllCourseFolderContentByScheduleTimeQuery
import com.student.competishun.curator.FindCourseFolderProgressQuery
import com.student.competishun.data.model.CalendarDate
import com.student.competishun.data.model.ScheduleData
import com.student.competishun.databinding.FragmentScheduleBinding
import com.student.competishun.ui.adapter.ScheduleAdapter
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.main.PdfViewerActivity
import com.student.competishun.ui.viewmodel.MyCoursesViewModel
import com.student.competishun.ui.viewmodel.VideourlViewModel
import com.student.competishun.utils.HelperFunctions
import com.student.competishun.utils.ToolbarCustomizationListener
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale
import java.time.ZonedDateTime
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@AndroidEntryPoint
class ScheduleFragment : Fragment(), ToolbarCustomizationListener {

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalendar(scheduleTime:String) {
        calendarSetUp = HorizontalCalendarSetUp()

        val currentMonth = calendarSetUp.setUpCalendarAdapter(
            binding.rvCalenderDates,
            requireContext(),
            onDateSelected = { calendarDate ->
                scrollToDate(calendarDate)
            }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupRecyclerView(contentList: List<FindAllCourseFolderContentByScheduleTimeQuery.FindAllCourseFolderContentByScheduleTime>) {
    Log.e("foldevontent" ,contentList.toString())
        var courseDate:CalendarDate = CalendarDate("","","",null)
        Log.e("foldevonten" ,courseDate.toString())

        val scheduleDataList = contentList.groupBy {
            val scheduledTime = convertIST(it.content.scheduled_time.toString())
            scheduledTime.dayOfWeek.toString().take(3) to scheduledTime.dayOfMonth.toString()
        }.map { (dateInfo, groupedContentList) ->
            val (dayOfWeek, dayOfMonth) = dateInfo
            ScheduleData(
                dayOfWeek,
                dayOfMonth,
                groupedContentList.map { content ->
                    ScheduleData.InnerScheduleItem(
                        content.parentFolderName?:"",
                        content.content.file_name,
                        formatTime(convertIST(content.content.scheduled_time.toString())),
                        convertLastDuration(formatTime(convertIST(content.content.scheduled_time.toString())),content.content.video_duration?.toLong()?:0),
                        content.content.file_url.toString(),
                        content.content.file_type.name,
                        content.content.id,
                        content.content.scheduled_time.toString()

                    )
                }
            )
        }


        scheduleAdapter = ScheduleAdapter(scheduleDataList, requireContext(),this)
        binding.rvCalenderSchedule.adapter = scheduleAdapter
        binding.rvCalenderSchedule.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        scrollToDate(courseDate)




        scheduleDataList.forEach { scheduleData ->
            Log.e("ScheduleDatakaj", scheduleData.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                     Log.e("scheduletimecu ","$extractedDate $it")
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

            }.onFailure { exception ->
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
      //  myCourseViewModel.getCourseFolderContent("08-27-2024", "10-30-2025", "31296a0b-6dea-42e5-b273-668744bf34a4")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertIST(dateString: String): ZonedDateTime {
        val zonedDateTime = ZonedDateTime.parse(dateString) // Adjust this according to your input format
        return zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)
        helperFunctions = HelperFunctions()
        val courseName  =  arguments?.getString("courseName")
        val courseId  =  arguments?.getString("courseId")?:""
        val courseStart  =  arguments?.getString("courseStart")
        val courseEnd  =  arguments?.getString("courseEnd")
        val courses =  arguments?.getString("courses")
        var start = helperFunctions.formatCourseDate(courseStart)
        var end = helperFunctions.formatCourseDate(courseEnd)
        Log.e("dataschec",dateFormate(start.toString()) + dateFormate(end.toString())  + courseId)
        scheduleData = ZonedDateTime.now()
        binding.backIconSchedule.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.clEmptySchedule.visibility = View.VISIBLE
        var starts = getDateBeforeDays(dateFormate(start),7)
        var ends = getDateAfterDays(dateFormate(end),7)
        myCourseViewModel.getCourseFolderContent(starts,ends, courseId)
        FindAllCourseFolderContentByScheduleTimeQuery()
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

    @RequiresApi(Build.VERSION_CODES.O)
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



    @RequiresApi(Build.VERSION_CODES.O)
    private fun scrollToDate(calendarDate: CalendarDate) {
        val position = scheduleAdapter.findPositionByDate(calendarDate.date)
        Log.e("scrollpostions $matchingPosition",position.toString())
        if (position != -1) {
            binding.rvCalenderSchedule.scrollToPosition(position)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTime(zonedDateTime: ZonedDateTime): String {
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

    fun getDateAfterDays(endDate: String, daysAfter: Int): String {
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
            val intent = Intent(context, PdfViewerActivity::class.java).apply {
                putExtra("PDF_URL", fileurl)
            }
            context?.startActivity(intent)
        }

    }
    fun videoUrlApi(viewModel: VideourlViewModel, folderContentId: String,name: String) {

        viewModel.fetchVideoStreamUrl(folderContentId, "360p")
         Log.e("foldfdfd",folderContentId)
        viewModel.videoStreamUrl.observe(viewLifecycleOwner, { signedUrl ->
            Log.d("Videourl", "Signed URL: $signedUrl")
            if (signedUrl != null) {
                val bundle = Bundle().apply {
                    putString("url", signedUrl)
                    putString("url_name", name)
                    putString("ContentId", folderContentId)
                }
                findNavController().navigate(R.id.mediaFragment, bundle)

            } else {
                // Handle error or null URL
            }
        })
    }


}


