package xyz.penpencil.competishun.ui.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.data.model.ScheduleData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.ItemCurrentSchedulesBinding
import xyz.penpencil.competishun.databinding.ItemCurrentSchedulesInnerChildBinding
import xyz.penpencil.competishun.utils.ToolbarCustomizationListener
import java.time.LocalDate

class ScheduleAdapter(private val scheduleItems: List<ScheduleData>, private val context: Context, private val toolbarListener: ToolbarCustomizationListener) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemCurrentSchedulesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    fun findPositionByDate(date: String): Int {
        val today = LocalDate.now().toString() // Convert today's date to string format
        val todayIndex = scheduleItems.indexOfFirst { it.date == today }
        if (todayIndex != -1) {
            return todayIndex
        } else {return scheduleItems.indexOfFirst { it.date == date }}
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

            val innerAdapter = InnerScheduleAdapter(scheduleItem.innerItems,toolbarListener, scheduleItem.duration)
            binding.rvScheduleInnerItem.adapter = innerAdapter
            binding.rvScheduleInnerItem.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        }
    }
    inner class InnerScheduleAdapter(
        private val innerItems: List<ScheduleData.InnerScheduleItem>,
        private val toolbarListener: ToolbarCustomizationListener,
        private val duration: Int
    ) : RecyclerView.Adapter<InnerScheduleAdapter.InnerViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
            val binding = ItemCurrentSchedulesInnerChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return InnerViewHolder(binding)
        }

        override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
            val innerItem = innerItems[position]
            holder.bind(innerItem, duration)
        }

        override fun getItemCount(): Int = innerItems.size

        inner class InnerViewHolder(private val binding: ItemCurrentSchedulesInnerChildBinding) : RecyclerView.ViewHolder(binding.root) {

            private val handler = Handler(Looper.getMainLooper())
            private val updateInterval: Long = 1000

            fun bind(innerItem: ScheduleData.InnerScheduleItem, duration: Int) {

                binding.tvSubjectName.text = innerItem.subject_name
                binding.tvTopicName.text = innerItem.topic_name
                startCountdownTimer(innerItem.scheduleTimer,innerItem.file_url , innerItem.fileType,innerItem.contentId)
                Log.e("sbAHGSHahsghaGHS", "bind: " +innerItem.scheduleTimer)
                if (innerItem.fileType=="VIDEO") {

                    binding.tvClassTimings.text = "${innerItem.lecture_start_time+"-"+innerItem.lecture_end_time}"
                    binding.tvHoursRemaining.text = ""
                    binding.videoIV.setImageResource(R.drawable.video_bg)
                }else if (innerItem.fileType=="PDF"){
                 //   startCountdownTimer(innerItem.scheduleTimer, innerItem.file_url, "PDF",innerItem.contentId)
                    binding.videoIV.setImageResource(R.drawable.pdf_bg)
                    binding.tvClassTimings.text = "${innerItem.lecture_start_time}"
                }
                val lectureStartTime = innerItem.lecture_start_time
                val lectureEndTime = innerItem.lecture_end_time

                if (innerItem.completedDuration!=0 && (innerItem.completedDuration == duration)){
                    //completed
                    binding.tvClassTimings.visibility = View.GONE
                    binding.tvClassStatus.visibility = View.VISIBLE
                    binding.tvClassStatus.text = "Class Attended"

                }else if (innerItem.completedDuration!=0){
                    //started
                    binding.tvClassStatus.visibility = View.VISIBLE
                    binding.tvClassTimings.visibility = View.GONE
                }else {
                    //not started
                    binding.tvClassStatus.visibility = View.GONE
                    binding.tvClassTimings.visibility = View.VISIBLE
                }

             /*   if (itemView=="Class Attended" || lectureStatus=="Class Missed" || lectureStatus=="Class Cancelled") {
                    binding.tvClassStatus.text = lectureStatus
                    binding.tvClassStatus.visibility = View.VISIBLE
                    binding.tvClassTimings.visibility = View.GONE
                    if (lectureStatus=="Class Cancelled") {
                        binding.tvClassStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                        binding.tvClassStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_class_cancelled, 0, 0, 0)
                        binding.view.visibility = View.VISIBLE
                        binding.dottedLine.visibility = View.GONE
                        binding.clLectureTimer.visibility = View.GONE
                        binding.clJoinLecture.visibility = View.GONE
                    } else if (lectureStatus=="Class Attended") {
                        binding.tvClassStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
                        binding.tvClassStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_circle_schedule, 0, 0, 0)
                        binding.clLectureTimer.visibility = View.GONE
                        binding.clJoinLecture.visibility = View.GONE
                    }else {
                        binding.tvClassStatus.setTextColor(ContextCompat.getColor(context, R.color.gray))
                        binding.tvClassStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_class_missed, 0, 0, 0)
                        binding.clLectureTimer.visibility = View.GONE
                        binding.clJoinLecture.visibility = View.GONE
                    }
                } else {
                    binding.tvClassStatus.visibility = View.GONE
                    binding.tvClassTimings.visibility = View.VISIBLE
                    binding.tvClassTimings.text = "$lectureStartTime - $lectureEndTime"
                    startCountdown(lectureStartTime, lectureEndTime)
                }*/
            }

          /*  private fun startCountdown(startTime: String, endTime: String) {
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

                            binding.clLectureTimer.visibility = View.VISIBLE
                            binding.clJoinLecture.visibility = View.GONE

                        } else if (currentMillis > endMillis) {
                            binding.clLectureTimer.visibility = View.GONE
                            binding.clJoinLecture.visibility = View.GONE

                        } else {
                            val elapsedMillis = currentMillis - startMillis
                            val elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)

                            binding.clLectureTimer.visibility = View.GONE
                            binding.clJoinLecture.visibility = View.VISIBLE
                            binding.tvClassStartedStatus.text = "Started ${elapsedMinutes} mins ago"
                        }

                        handler.postDelayed(this, updateInterval)
                    }
                }

                handler.post(updateTimeRunnable)
            }

            // Function to parse the timestamp and calculate the remaining time
            fun calculateTimeRemaining(targetTimestamp: String): String {
                // Define the formatter for parsing the timestamp
                val formatter = java.time.format.DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("UTC"))

                // Parse the timestamp into a ZonedDateTime object
                val targetTime = ZonedDateTime.parse(targetTimestamp, formatter)

                // Get the current time
                val now = ZonedDateTime.now(ZoneId.of("UTC"))

                // Calculate the duration between the current time and the target time
                val duration = Duration.between(now, targetTime)

                // Handle cases where the target time is in the past
                if (duration.isNegative) {
                    return "Time's up!"
                }

                val hours = duration.toHours()
                val minutes = duration.toMinutes() % 60
                val seconds = duration.seconds % 60
                duration.seconds
                // Format the remaining time as a string
                return String.format("Time Remaining: %02d:%02d:%02d", hours, minutes, seconds)
            }
*/
            private fun startCountdownTimer(targetTimestamp: String, fileUrl: String, fileType: String, ContentId: String) {
                val utcZone = ZoneId.of("UTC")
                val istZone = ZoneId.of("UTC")

                val targetTimeUtc = ZonedDateTime.parse(targetTimestamp, DateTimeFormatter.ISO_DATE_TIME.withZone(utcZone))

                val targetTimeIst = targetTimeUtc.withZoneSameInstant(istZone)

                val nowInIst = ZonedDateTime.now(istZone)
                val initialDuration = Duration.between(nowInIst, targetTimeIst).toMillis()

                val timer = object : CountDownTimer(initialDuration, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val secondsRemaining = millisUntilFinished / 1000
                        val days = secondsRemaining / 86400 // 60 * 60 * 24
                        val hours = (secondsRemaining % 86400) / 3600
                        val minutes = (secondsRemaining % 3600) / 60
                        val seconds = secondsRemaining % 60
                        val totalHours = (days * 24) + hours
                        if (binding.clLectureTimer.visibility != View.VISIBLE) {
                            binding.clLectureTimer.visibility = View.VISIBLE
                            binding.clJoinLecture.visibility = View.GONE
                        }
                        binding.tvHoursRemaining.text = "$totalHours"
                        binding.tvMinRemaining.text = "$minutes"
                        binding.tvSecRemaining.text = "$seconds"
                    }

                    override fun onFinish() {
                        val nowInIst = ZonedDateTime.now(istZone)
                        Log.e("CountdownTimer", "Target Time: $targetTimeIst | onFinish: $nowInIst")

                        // Calculate time passed since the target time
                        val durationSinceEnd = Duration.between(targetTimeIst, nowInIst)
                        val daysPassed = durationSinceEnd.toDays()
                        val hoursPassed = durationSinceEnd.toHours() % 24
                        val minutesPassed = durationSinceEnd.toMinutes() % 60

                        val message = when {
                            daysPassed > 0 -> "Started $daysPassed days ago"
                            hoursPassed > 0 -> "Started $hoursPassed hours ago"
                            else -> "Started $minutesPassed minutes ago"
                        }
                        binding.tvClassStartedStatus.text = message
                        binding.clLectureTimer.visibility = View.GONE
                        binding.clJoinLecture.visibility = View.VISIBLE

                        // Handle the join lecture button click
                        binding.btnJoinLecture.setOnClickListener {
                            Log.e("File Info", "$fileUrl | Type: $fileType | Content ID: $ContentId")
                            toolbarListener.onCustomizeToolbar(fileUrl, fileType, ContentId)
                        }
                    }
                }

                // Start the timer
                timer.start()
            }
        }
    }


}
