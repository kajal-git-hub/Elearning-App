package com.student.competishun.ui.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.SubjectContentItem
import com.student.competishun.databinding.ItemCourseContentBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class SubjectContentAdapter(
    private val items: List<SubjectContentItem>,
    private val onItemClicked: (SubjectContentItem) -> Unit
) : RecyclerView.Adapter<SubjectContentAdapter.SubjectContentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectContentViewHolder {
        val binding =
            ItemCourseContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectContentViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: SubjectContentViewHolder, position: Int) {
        val course = items[position]
        holder.bind(course)
        holder.itemView.setOnClickListener {
            onItemClicked(course)
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun showDateIfFutureOrToday(dateString: String): Boolean {
        // Define the primary date format pattern
        val dateString = dateString.replace("Sept", "Sep").trim()

        val primaryFormatter = DateTimeFormatter.ofPattern("dd MMM, yy", Locale.ENGLISH)
        // Define a fallback format pattern if needed
        val fallbackFormatter = DateTimeFormatter.ofPattern("dd MMM yy", Locale.ENGLISH)

        return try {
            // Try to parse using the primary formatter
            val date = LocalDate.parse(dateString.trim(), primaryFormatter)
            // Get today's date
            val today = LocalDate.now()
            // Compare the dates
            Log.e("adfdf$today",date.toString())
            date.isBefore(today) || date.isEqual(today)
        } catch (e: DateTimeParseException) {
            // Log the exception and try the fallback format
            Log.e("DateParsingError", "Primary format parsing error: ${e.message}. Input date string: '$dateString'")
            try {
                // Try parsing using the fallback formatter
                val date = LocalDate.parse(dateString.trim(), fallbackFormatter)
                val today = LocalDate.now()
                Log.e("adfdf $today",date.toString())
                date.isBefore(today) || date.isEqual(today)
            } catch (fallbackException: DateTimeParseException) {
                // Log the fallback exception and return false if parsing fails
                Log.e("DateParsingError", "Fallback format parsing error: ${fallbackException.message}. Input date string: '$dateString'")
                false
            }
        }
    }


    override fun getItemCount(): Int = items.size

    inner class SubjectContentViewHolder(private val binding: ItemCourseContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: SubjectContentItem) {
            binding.tvChapterNumber.text = String.format("%02d", item.chapterNumber)
            binding.tvTopicName.text = item.topicName
            binding.customTopicProgress.progress = item.progressPer
            binding.tvTopicDescription.text = item.topicDescription + " Learning Material"
            binding.CustomTopicPercentCompleted.text = item.progressPer.toString() + "% Completed"
           Log.e("datead",item.locktime)
            if (showDateIfFutureOrToday(item.locktime)) {
                Log.e("datead True",item.locktime)
                binding.IvlockImage.setImageResource(R.drawable.arrow_right__1_)
            }else{
                Log.e("datead false",item.locktime)
                binding.IvlockImage.setImageResource(R.drawable.frame_1707481080)
            }

        }
    }
}
