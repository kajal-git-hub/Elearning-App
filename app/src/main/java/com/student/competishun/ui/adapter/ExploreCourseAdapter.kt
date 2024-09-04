package com.student.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.student.competishun.R
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

        Glide.with(holder.itemView.context)
            .load(course.bannerImage)
            .placeholder(R.drawable.rectangle_1072)
            .error(R.drawable.frame_1707480074)
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .into(holder.binding.ivPurchased)

        holder.itemView.setOnClickListener {
            onItemClicked(course)
            Log.e("folder_Idada:", course.folderIds.toString())
        }
    }

    override fun getItemCount(): Int = courses.size

    class ExploreCourseViewHolder(val binding: ExploreCourseItemBinding) : RecyclerView.ViewHolder(binding.root) {
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

            binding.ivFreeTag.visibility = View.GONE
            binding.customProgressIndicator.progress = course.percentCompleted
        }
    }
}
