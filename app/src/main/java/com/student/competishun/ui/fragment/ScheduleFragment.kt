package com.student.competishun.ui.fragment

import HorizontalCalendarSetUp
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.curator.FindAllCourseFolderContentByScheduleTimeQuery
import com.student.competishun.data.model.CalendarDate
import com.student.competishun.data.model.ScheduleData
import com.student.competishun.databinding.FragmentScheduleBinding
import com.student.competishun.ui.adapter.ScheduleAdapter
import com.student.competishun.ui.main.HomeActivity
import com.student.competishun.ui.viewmodel.MyCoursesViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private val binding by lazy {
        FragmentScheduleBinding.inflate(layoutInflater)
    }

    private lateinit var calendarSetUp: HorizontalCalendarSetUp
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val myCourseViewModel: MyCoursesViewModel by viewModels()
    lateinit var scheduleData:ZonedDateTime
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // setupCalendar()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalendar(scheduleTime:String) {
        calendarSetUp = HorizontalCalendarSetUp()

        val currentMonth = calendarSetUp.setUpCalendarAdapter(
            binding.rvCalenderDates,
            requireContext(),
            onDateSelected = { calendarDate ->
              //  scrollToDate(calendarDate)
            }
        )
        Log.e("schedule57 $scheduleTime", convertIST(scheduleTime).month.toString() )
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
        calendarSetUp.scrollToSpecificDate(binding.rvCalenderDates, convertIST(scheduleTime))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupRecyclerView(contentList: List<FindAllCourseFolderContentByScheduleTimeQuery.FindAllCourseFolderContentByScheduleTime>) {
    Log.e("foldevontent" ,contentList.toString())
        var courseDate:CalendarDate = CalendarDate("","","",null)


        val sampleData =

          contentList.map { content ->
              Log.e("coursefolderntent" ,convertCalender(content.folder.scheduled_time.toString()).toString())
                Log.e("courseDatentent" ,courseDate.toString())
            ScheduleData(
                convertIST(content.folder.scheduled_time.toString()).dayOfWeek.toString().take(3), convertIST(content.folder.scheduled_time.toString()).dayOfMonth.toString(),
                contentList.map { content ->
                    courseDate = convertCalender(content.folder.scheduled_time.toString())
                    ScheduleData.InnerScheduleItem(
                        content.folder.name,
                        content.file_name,
                       formatTime(convertIST(content.folder.scheduled_time.toString())),
                        "11:00 AM",
                        ""
                    )
                }
            )
   }
        val scheduleDataList = contentList.groupBy {
            val scheduledTime = convertIST(it.folder.scheduled_time.toString())
            scheduledTime.dayOfWeek.toString().take(3) to scheduledTime.dayOfMonth.toString()
        }.map { (dateInfo, groupedContentList) ->
            val (dayOfWeek, dayOfMonth) = dateInfo
            ScheduleData(
                dayOfWeek,
                dayOfMonth,
                groupedContentList.map { content ->
                    ScheduleData.InnerScheduleItem(
                        content.folder.name,
                        content.file_name,
                        formatTime(convertIST(content.folder.scheduled_time.toString())),
                        "11:00 AM", // Replace with actual end time logic
                        "" // Replace with any extra info if needed
                    )
                }
            )
        }


        scheduleAdapter = ScheduleAdapter(scheduleDataList, requireContext())
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
                    Log.e("timea $index", scheduleContent.folder.scheduled_time.toString())
                }
                data.findAllCourseFolderContentByScheduleTime.forEach { schedulecontent->
                // Log.e("timea",schedulecontent.s.toString())
                 schedulecontent.folder.scheduled_time.let {
                     setupCalendar(it.toString())

                 }
                    Log.e("schedule57 $scheduleData", convertIST(scheduleData.toString()).toString())
                    binding.tvCalenderCurrentMonth.text = "${convertIST(scheduleData.toString()).month} ${convertIST(scheduleData.toString()).year}"

                }

            }.onFailure { exception ->
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
        myCourseViewModel.getCourseFolderContent("08-27-2024", "10-30-2025", "30321205-bc5a-4d98-b8bf-79119b1e2ba4")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertIST(dateString: String): ZonedDateTime {
        val zonedDateTime = ZonedDateTime.parse(dateString) // Adjust this according to your input format
        return zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (activity as? HomeActivity)?.showBottomNavigationView(false)
        (activity as? HomeActivity)?.showFloatingButton(false)

        scheduleData = ZonedDateTime.now()
        binding.backIconSchedule.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.clEmptySchedule.visibility = View.VISIBLE
        FindAllCourseFolderContentByScheduleTimeQuery()
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



    private fun scrollToDate(calendarDate: CalendarDate) {
        val position = scheduleAdapter.findPositionByDate(calendarDate.date)
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
}


