package com.student.competishun.ui.fragment

import HorizontalCalendarSetUp
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.student.competishun.curator.FindAllCourseFolderContentByScheduleTimeQuery
import com.student.competishun.data.model.CalendarDate
import com.student.competishun.data.model.ScheduleData
import com.student.competishun.databinding.FragmentScheduleBinding
import com.student.competishun.ui.adapter.ScheduleAdapter
import com.student.competishun.ui.viewmodel.MyCoursesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private val binding by lazy {
        FragmentScheduleBinding.inflate(layoutInflater)
    }

    private lateinit var calendarSetUp: HorizontalCalendarSetUp
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val myCourseViewModel: MyCoursesViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupCalendar()
        return binding.root
    }

    private fun setupCalendar() {
        calendarSetUp = HorizontalCalendarSetUp()

        val currentMonth = calendarSetUp.setUpCalendarAdapter(
            binding.rvCalenderDates,
            requireContext(),
            onDateSelected = { calendarDate ->
                scrollToDate(calendarDate)
            }
        )
        binding.tvCalenderCurrentMonth.text = currentMonth

        calendarSetUp.setUpCalendarPrevNextClickListener(
            binding.rvCalenderDates,
            binding.arrowRightCalender,
            binding.arrowLeftCalender,
            requireContext(),
            { newMonth ->
                binding.tvCalenderCurrentMonth.text = newMonth
            },
            { calendarDate ->
                scrollToDate(calendarDate)
            }
        )
    }

    private fun setupRecyclerView(findAllCourseFolderContentByScheduleTime: List<FindAllCourseFolderContentByScheduleTimeQuery.FindAllCourseFolderContentByScheduleTime>) {
      findAllCourseFolderContentByScheduleTime.get(0).folder.name

        val transformedData = findAllCourseFolderContentByScheduleTime.map { folderContent ->
            ScheduleData(
                day = "",
                date = folderContent.scheduled_time.toString(),
                innerItems = folderContent.folder.folder_content?.map { item ->
                    ScheduleData.InnerScheduleItem(
                        subject_name = item.file_name,
                        topic_name = item.description?:"",
                        lecture_start_time = "03:12:34 PM",
                        lecture_end_time = "09:12:34 PM",
                        lecture_status = "Scheduled"
                    )
                }?: emptyList()

            )
        }
        val sampleData = listOf(
            ScheduleData(
                "Sat", "03", listOf(
                    ScheduleData.InnerScheduleItem(
                        "Math",
                        "Introduction to Thermodynamics",
                        "10:00 AM",
                        "11:00 AM",
                        "Class Attended"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "Science",
                        "Introduction to Thermodynamics",
                        "8:00 PM",
                        "10:00 PM",
                        "Class Missed"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "Science",
                        "Introduction to Thermodynamics",
                        "8:00 PM",
                        "9:00 PM",
                        "Class Cancelled"
                    ),
                )
            ),
            ScheduleData(
                "Sun", "04", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "05:00 PM",
                        "06:00 PM",
                        "On Going"
                    )
                )
            ),
            ScheduleData(
                "Mon", "05", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Tue", "06", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Wed", "07", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Thu", "08", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Fri", "09", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Sat", "10", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Sun", "11", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Mon", "12", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Tue", "13", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Wed", "14", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Thu", "15", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Fri", "16", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Sat", "17", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Sun", "18", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Mon", "19", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            ),
            ScheduleData(
                "Tue", "20", listOf(
                    ScheduleData.InnerScheduleItem(
                        "English",
                        "Introduction to Thermodynamics",
                        "7:00 PM",
                        "8:00 PM",
                        "Not Started Yet"
                    ),
                    ScheduleData.InnerScheduleItem(
                        "History",
                        "Introduction to Thermodynamics",
                        "04:00 PM",
                        "05:00 PM",
                        "Class Attended"
                    )
                )
            )
        )

        scheduleAdapter = ScheduleAdapter(sampleData, requireContext())
        binding.rvCalenderSchedule.adapter = scheduleAdapter
        binding.rvCalenderSchedule.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Scroll to the current date by default
        val currentDate = calendarSetUp.getCurrentDate()
        scrollToDate(currentDate)
    }

    fun FindAllCourseFolderContentByScheduleTimeQuery(){
        myCourseViewModel.courseFolderContent.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                 setupRecyclerView(data.findAllCourseFolderContentByScheduleTime)

            }.onFailure { exception ->
                Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
        myCourseViewModel.getCourseFolderContent("08-29-2024", "10-30-2025", "c00d3ee9-dc1e-47c7-894a-5f63535c1fdc")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIconSchedule.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        FindAllCourseFolderContentByScheduleTimeQuery()
    }


    private fun scrollToDate(calendarDate: CalendarDate) {
        val position = scheduleAdapter.findPositionByDate(calendarDate.date)
        if (position != -1) {
            binding.rvCalenderSchedule.scrollToPosition(position)
        }
    }
}


