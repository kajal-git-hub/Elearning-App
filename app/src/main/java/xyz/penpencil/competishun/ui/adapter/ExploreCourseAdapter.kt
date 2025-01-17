package xyz.penpencil.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.student.competishun.curator.MyCoursesQuery
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.ExploreCourseItemBinding

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
        val complementaryCourseId = course.id
        val complementaryCourse = courses.find { it.complementary_course == complementaryCourseId }
        if (complementaryCourse != null) {
            // Set the complementary course banner
            holder.binding.ivFreeTag.visibility = View.VISIBLE
        } else {
            // Set the regular course banner
            holder.binding.ivFreeTag.visibility = View.GONE
        }
        holder.bind(course, progress)
        Log.e("complementryno",course.complementary_course.toString())
        Log.e("complementrynot",course.id.toString())

        Glide.with(holder.itemView.context)
            .load(course.banner_image)
            .placeholder(R.drawable.rectangle_1072)
            .error(R.drawable.default_image)
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            .into(holder.binding.ivPurchased)

        Log.e("complementaryadd",course.complementary_course.toString())
        Log.e("courseIDs",course.id.toString())

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
            Log.e("view complementry ",course.complementary_course.toString())
            Log.e("view course",course.id.toString())
            if (course.id == course.complementary_course){ Log.e("isnidecontdeion ",course.complementary_course.toString())}
            binding.tvTag3ExploreCourse.text = course.target_year.toString()
            var coursePercen = progress?.completionPercentage?.toInt()?:0
            Log.e("coursefef",coursePercen.toString())
            binding.tvOngoing.text = if( coursePercen > 0){ "Ongoing"} else if (coursePercen > 100){"Completed"}else {"Not Started"}

            binding.tvPercentCompleted.text = buildString {
                append(String.format("%d", progress?.completionPercentage?.
                toInt() ?: 0))
                append("%")
            }
            binding.customProgressIndicator.progress = progress?.completionPercentage?.toInt()?:0
        }
    }
}
