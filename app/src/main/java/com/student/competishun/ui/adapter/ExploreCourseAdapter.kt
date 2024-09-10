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
    private val onItemClicked: (MyCoursesQuery.Course,List<MyCoursesQuery.Folder1?>, List<Double>, ) -> Unit// Updated callback
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
            val subFolder = progress.subfolderDurations
                ?.map { it.folder } ?: emptyList()
            val subFolderCompletionPercentages = progress.subfolderDurations
                ?.map { it.completionPercentage } ?: emptyList()
            val subFolders = progress.subfolderDurations
                ?.map { it.folder } ?: emptyList()
            val folderperc = progress.subfolderDurations
                ?.map { it.folder?.id } ?: emptyList()
            val folderCounts = progress.subfolderDurations
                ?.map { it.folder?.folder_count?:"" } ?: emptyList()

            val folderNames = progress.subfolderDurations
                ?.map { it.folder?.name?:"" } ?: emptyList()
            val progresPercentage = progress.completionPercentage?:0.0


            onItemClicked(
                course,
                subFolders,
                subFolderCompletionPercentages,

            )
            Log.e("folder_Id $folderperc: cont $folderCounts", folderNames.toString())
            Log.e("percentagess $progresPercentage:", subFolderCompletionPercentages.toString())
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
