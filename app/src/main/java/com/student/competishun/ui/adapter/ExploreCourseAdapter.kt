package com.student.competishun.ui.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.ExploreCourse
import com.student.competishun.databinding.ExploreCourseItemBinding

class ExploreCourseAdapter(
    private val courses: List<ExploreCourse>,
    private val onItemClicked: (ExploreCourse) -> Unit
) : RecyclerView.Adapter<ExploreCourseAdapter.ExploreCourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreCourseViewHolder {
        val binding = ExploreCourseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExploreCourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExploreCourseViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course)
        holder.itemView.setOnClickListener {
            onItemClicked(course)
            Log.e("folder_Idada:", course.folderIds.toString())
        }
    }

    override fun getItemCount(): Int = courses.size

    class ExploreCourseViewHolder(private val binding: ExploreCourseItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: ExploreCourse) {
            binding.tvExploreCourseName.text = course.name
            binding.tvTag1ExploreCourse.text = course.className
            binding.tvTag2ExploreCourse.text = course.courseType
            binding.tvTag3ExploreCourse.text = course.target
            binding.tvOngoing.text = course.ongoingStatus
            binding.tvPercentCompleted.text = buildString {
                append(course.percentCompleted.toString())
                append("%")
            }

            binding.ivFreeTag.visibility =  View.GONE
           // if (course.hasFreeFolder) View.VISIBLE else
            binding.customProgressIndicator.progress = course.percentCompleted
        }
    }
}
