package com.student.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.request.target.Target
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.student.competishun.R
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.utils.HelperFunctions

class RecommendedCoursesAdapter(
    private val items: List<AllCourseForStudentQuery.Course>,
    private val lectureCounts: Map<String, Int>,
    private val onItemClick: (AllCourseForStudentQuery.Course,List<String>?) -> Unit
) : RecyclerView.Adapter<RecommendedCoursesAdapter.CourseViewHolder>() {

    private lateinit var helperFunctions: HelperFunctions

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommended_course_item, parent, false)
        helperFunctions = HelperFunctions()
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = items[position]
        val recommendCourseTags = course.course_tags
        Log.d("recommendCourseTags", recommendCourseTags.toString())

        holder.recommendedClass.apply {
            text = recommendCourseTags?.getOrNull(0) ?: ""
            visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
        }

        holder.tvTag2.apply {
            text = recommendCourseTags?.getOrNull(1) ?: ""
            visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
        }

        holder.tvLastField.apply {
            text = recommendCourseTags?.getOrNull(2) ?: ""
            visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
        }



        holder.courseName.text = "${course.name}  ${course.academic_year}"

        if (course.price != null && course.discount != null) {
            val discountPercent = helperFunctions.calculateDiscountPercentage(course.price, course.discount)
            holder.discount.text = "${discountPercent.toInt()}% off"
            holder.discountPrice.text = "₹${course.discount}"
            holder.originalPrice.text = "₹${course.price}"
            holder.percentConstraint.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)

                .load(course.banner_image)
                .placeholder(R.drawable.rectangle_1072)
                .error(R.drawable.default_image)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(holder.bannerImage)


        }else if (course.price!=null && course.discount==null){
            holder.discountPrice.text = "₹${course.price}"
            holder.originalPrice.visibility = View.GONE

        }else if(course.price==course.discount){
            holder.percentConstraint.visibility = View.GONE
        }
        holder.targetYear.text = "Target ${course.target_year}"
        holder.startDate.text = "Starts On: "+helperFunctions.formatCourseDate(course.course_start_date.toString())
        holder.endDate.text = "Ends On: "+helperFunctions.formatCourseDate(course.course_end_date.toString())
        holder.lectureCount.text = "Lectures: ${(lectureCounts[course.id] ?: 0)}"
        holder.quizCount.text = "Validity: "+helperFunctions.formatCourseDate(course.course_validity_end_date.toString())
        holder.itemView.setOnClickListener {
            onItemClick(course,recommendCourseTags)
        }
    }

    override fun getItemCount(): Int = items.size

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val courseName: TextView = view.findViewById(R.id.tvRecommendedCourseName)
        val discount: TextView = view.findViewById(R.id.tvDiscount)
        val recommendedClass : TextView = view.findViewById(R.id.recommendedClass)
        val targetYear: TextView = view.findViewById(R.id.tvTarget)
        val startDate: MaterialTextView = view.findViewById(R.id.tvStartDate)
        val endDate: MaterialTextView = view.findViewById(R.id.tvEndDate)
        val lectureCount: TextView = view.findViewById(R.id.tvLectureNo)
        val quizCount: TextView = view.findViewById(R.id.tvQuizTests)
        val originalPrice: TextView = view.findViewById(R.id.orgPrice)
        val discountPrice: TextView = view.findViewById(R.id.dicountPrice)
        val bannerImage:ImageView = view.findViewById(R.id.recommendbanner)
        val tvTag2:TextView = view.findViewById(R.id.tvTag2)
        val percentConstraint :ConstraintLayout = view.findViewById(R.id.cl_percentOff)

        val tvLastField:TextView = view.findViewById(R.id.tvLastField)
    }
}
