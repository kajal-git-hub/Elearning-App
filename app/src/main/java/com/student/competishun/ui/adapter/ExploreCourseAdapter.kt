package com.student.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.student.competishun.R
import com.student.competishun.curator.MyCoursesQuery
import com.student.competishun.data.model.ExploreCourse
import com.student.competishun.databinding.ExploreCourseItemBinding

class ExploreCourseAdapter(
    private val courses: List<MyCoursesQuery.Course>,
    private val progressList: List<MyCoursesQuery.Progress>, // List to hold progress information
    private val onItemClicked: (MyCoursesQuery.Course, List<String>, List<String>, List<Double>,List<String>) -> Unit// Updated callback
) : RecyclerView.Adapter<ExploreCourseAdapter.ExploreCourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreCourseViewHolder {
        val binding = ExploreCourseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExploreCourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExploreCourseViewHolder, position: Int) {
        val course = courses[position]
        val progress = progressList[position]
        holder.bind(course, progress)

        Glide.with(holder.itemView.context)
            .load(course.banner_image)
            .placeholder(R.drawable.rectangle_1072)
            .error(R.drawable.default_image)
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .into(holder.binding.ivPurchased)

        holder.itemView.setOnClickListener {
            val completionPercentages = progress.subfolderDurations
                ?.map { it.completionPercentage } ?: emptyList()
            val folderIds = course.folder
                ?.filter { it.parent_folder_id == null && !it.name.startsWith("Free", ignoreCase = true) }
                ?.map { it.id } ?: emptyList()
            val folderCounts = course.folder
                ?.filter { it.parent_folder_id == null && !it.name.startsWith("Free", ignoreCase = true) }
                ?.map { it.folder_count?:"" } ?: emptyList()

            val folderNames = course.folder
                ?.filter { it.parent_folder_id == null && !it.name.startsWith("Free", ignoreCase = true) }
                ?.map { it.name } ?: emptyList()
            onItemClicked(
                course,
                folderIds,
                folderNames,
                completionPercentages,
               folderCounts
            )
            Log.e("folder_Idada:", folderIds.toString())
            Log.e("folder_Name:", folderNames.toString())
        }
    }

    override fun getItemCount(): Int = courses.size

    class ExploreCourseViewHolder(val binding: ExploreCourseItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: MyCoursesQuery.Course,progress: MyCoursesQuery.Progress?) {
            binding.tvExploreCourseName.text = course.name
            binding.tvTag2ExploreCourse.text = course.exam_type.toString()

            if(course.course_class.toString()=="ELEVENTH"){
                binding.tvTag1ExploreCourse.text = "11th"
            }else if(course.course_class.toString()=="TWELFTH"){
                binding.tvTag1ExploreCourse.text = "12th"
            }else if (course.course_class.toString()=="TWELFTH_PLUS"){
                binding.tvTag1ExploreCourse.text = "12+"
            }

            binding.tvTag3ExploreCourse.text = course.target_year.toString()
            binding.tvOngoing.text = course.status.toString()
            binding.tvPercentCompleted.text = buildString {
                append(String.format("%.2f", progress?.completionPercentage))
                append("%")
            }

            binding.ivFreeTag.visibility = View.GONE
            binding.customProgressIndicator.progress = progress?.completionPercentage?.toInt()?:0
        }
    }
}
