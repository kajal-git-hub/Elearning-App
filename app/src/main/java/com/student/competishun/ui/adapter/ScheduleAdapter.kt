package com.student.competishun.ui.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.ScheduleData
import com.student.competishun.databinding.ItemCurrentSchedulesBinding
import com.student.competishun.databinding.ItemCurrentSchedulesInnerChildBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ScheduleAdapter(private val scheduleItems: List<ScheduleData>, private val context: Context) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemCurrentSchedulesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    fun findPositionByDate(date: String): Int {
        return scheduleItems.indexOfFirst { it.date == date }
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val scheduleItem = scheduleItems[position]
        holder.bind(scheduleItem)
    }

    override fun getItemCount(): Int = scheduleItems.size

    inner class ScheduleViewHolder(private val binding: ItemCurrentSchedulesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(scheduleItem: ScheduleData) {
            binding.tvDay.text = scheduleItem.day
            binding.tvDate.text = scheduleItem.date

            val innerAdapter = InnerScheduleAdapter(scheduleItem.innerItems,context)
            binding.rvScheduleInnerItem.adapter = innerAdapter
            binding.rvScheduleInnerItem.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        }
    }

    inner class InnerScheduleAdapter(private val innerItems: List<ScheduleData.InnerScheduleItem>,private val context: Context) : RecyclerView.Adapter<InnerScheduleAdapter.InnerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
            val binding = ItemCurrentSchedulesInnerChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return InnerViewHolder(binding)
        }

        override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
            val innerItem = innerItems[position]
            holder.bind(innerItem)
        }

        override fun getItemCount(): Int = innerItems.size

        inner class InnerViewHolder(private val binding: ItemCurrentSchedulesInnerChildBinding) : RecyclerView.ViewHolder(binding.root) {

            private val handler = Handler(Looper.getMainLooper())
            private val updateInterval: Long = 1000

            fun bind(innerItem: ScheduleData.InnerScheduleItem) {
                binding.tvSubjectName.text = innerItem.subject_name
                binding.tvTopicName.text = innerItem.topic_name

                val lectureStartTime = innerItem.lecture_start_time
                val lectureEndTime = innerItem.lecture_end_time
                val lectureStatus = innerItem.lecture_status

                if (lectureStatus=="Class Attended" || lectureStatus=="Class Missed" || lectureStatus=="Class Cancelled") {
                    binding.tvClassStatus.text = lectureStatus
                    binding.tvClassStatus.visibility = View.VISIBLE
                    binding.tvClassTimings.visibility = View.GONE
                    if (lectureStatus=="Class Cancelled") {
                        binding.tvClassStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                        binding.tvClassStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_class_cancelled, 0, 0, 0)
                        binding.clDownloadLectureMaterial.visibility = View.GONE
                        binding.view.visibility = View.VISIBLE
                        binding.dottedLine.visibility = View.GONE
                        binding.clLectureTimer.visibility = View.GONE
                        binding.clJoinLecture.visibility = View.GONE
                    } else if (lectureStatus=="Class Attended") {
                        binding.tvClassStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
                        binding.tvClassStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_circle_schedule, 0, 0, 0)
                        binding.clDownloadLectureMaterial.visibility = View.VISIBLE
                        binding.clLectureTimer.visibility = View.GONE
                        binding.clJoinLecture.visibility = View.GONE
                    }else {
                        binding.tvClassStatus.setTextColor(ContextCompat.getColor(context, R.color.gray))
                        binding.tvClassStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_class_missed, 0, 0, 0)
                        binding.clDownloadLectureMaterial.visibility = View.VISIBLE
                        binding.clLectureTimer.visibility = View.GONE
                        binding.clJoinLecture.visibility = View.GONE
                    }
                } else {
                    binding.tvClassStatus.visibility = View.GONE
                    binding.tvClassTimings.visibility = View.VISIBLE
                    binding.tvClassTimings.text = "$lectureStartTime - $lectureEndTime"
                    startCountdown(lectureStartTime, lectureEndTime)
                }
            }

            private fun startCountdown(startTime: String, endTime: String) {
                val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val startTimeParsed: Date = dateFormat.parse(startTime) ?: return
                val endTimeParsed: Date = dateFormat.parse(endTime) ?: return

                val calendar = Calendar.getInstance()
                val currentMillis = System.currentTimeMillis()
                calendar.timeInMillis = currentMillis

                val startCalendar = Calendar.getInstance().apply {
                    time = startTimeParsed
                    set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                    set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
                }
                val startMillis = startCalendar.timeInMillis

                val endCalendar = Calendar.getInstance().apply {
                    time = endTimeParsed
                    set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                    set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
                }
                val endMillis = endCalendar.timeInMillis

                val updateTimeRunnable = object : Runnable {
                    override fun run() {
                        val currentMillis = System.currentTimeMillis()

                        if (currentMillis < startMillis) {
                            val remainingMillis = startMillis - currentMillis
                            val hours = TimeUnit.MILLISECONDS.toHours(remainingMillis)
                            val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60
                            val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis) % 60

                            binding.tvHoursRemaining.text = String.format("%02d", hours)
                            binding.tvMinRemaining.text = String.format("%02d", minutes)
                            binding.tvSecRemaining.text = String.format("%02d", seconds)

                            binding.clDownloadLectureMaterial.visibility = View.GONE
                            binding.clLectureTimer.visibility = View.VISIBLE
                            binding.clJoinLecture.visibility = View.GONE

                        } else if (currentMillis > endMillis) {
                            binding.clDownloadLectureMaterial.visibility = View.VISIBLE
                            binding.clLectureTimer.visibility = View.GONE
                            binding.clJoinLecture.visibility = View.GONE

                        } else {
                            val elapsedMillis = currentMillis - startMillis
                            val elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)

                            binding.clDownloadLectureMaterial.visibility = View.GONE
                            binding.clLectureTimer.visibility = View.GONE
                            binding.clJoinLecture.visibility = View.VISIBLE
                            binding.tvClassStartedStatus.text = "Started ${elapsedMinutes} mins ago"
                        }

                        handler.postDelayed(this, updateInterval)
                    }
                }

                handler.post(updateTimeRunnable)
            }

        }
    }

}
