package com.student.competishun.ui.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.SubjectContentItem
import com.student.competishun.databinding.ItemCourseContentBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    override fun onBindViewHolder(holder: SubjectContentViewHolder, position: Int) {
        val course = items[position]
        holder.bind(course)
        holder.itemView.setOnClickListener {
            onItemClicked(course)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDateIfFutureOrToday(dateString: String):Boolean {
        // Define the date format (for "06 Sept, 24")
        val formatter = DateTimeFormatter.ofPattern("dd MMM, yy", Locale.ENGLISH)

        // Parse the string to LocalDate
        val date = LocalDate.parse(dateString, formatter)

        // Get today's date
        val today = LocalDate.now()

        // Compare the dates
        if (date.isAfter(today) || date.isEqual(today)) {
           return true
        } else {
           return false
        }
    }

    override fun getItemCount(): Int = items.size

    inner class SubjectContentViewHolder(private val binding: ItemCourseContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: SubjectContentItem) {
            binding.tvChapterNumber.text = String.format("%02d", item.chapterNumber)
            binding.tvTopicName.text = item.topicName
            binding.tvTopicDescription.text = item.topicDescription
           Log.e("datead",item.locktime)
            if (showDateIfFutureOrToday(item.locktime)) {
                binding.IvlockImage.visibility = View.GONE
            }

        }
    }
}
