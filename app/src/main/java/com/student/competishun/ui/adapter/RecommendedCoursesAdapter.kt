package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.RecommendedCourseDataModel
import com.google.android.material.textview.MaterialTextView

class RecommendedCoursesAdapter(
    private val courses: List<RecommendedCourseDataModel>
) : RecyclerView.Adapter<RecommendedCoursesAdapter.CourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommended_course_item, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.bind(course)
    }

    override fun getItemCount(): Int = courses.size

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val discount: MaterialTextView = itemView.findViewById(R.id.tvDiscount)
        private val courseName: MaterialTextView = itemView.findViewById(R.id.tvRecommendedCourseName)
        private val tag1: MaterialTextView = itemView.findViewById(R.id.tvTag1)
        private val tag2: MaterialTextView = itemView.findViewById(R.id.tvTag2)
        private val tag3: MaterialTextView = itemView.findViewById(R.id.tvTag3)
        private val startDate: MaterialTextView = itemView.findViewById(R.id.tvStartDate)
        private val endDate: MaterialTextView = itemView.findViewById(R.id.tvEndDate)
        private val lectureCount: MaterialTextView = itemView.findViewById(R.id.tvLectureNo)
        private val quizCount: MaterialTextView = itemView.findViewById(R.id.tvQuizTests)
        private val originalPrice: MaterialTextView = itemView.findViewById(R.id.orgPrice)
        private val discountPrice: MaterialTextView = itemView.findViewById(R.id.dicountPrice)

        fun bind(course: RecommendedCourseDataModel) {
            discount.text = course.discount
            courseName.text = course.courseName
            tag1.text = course.tag1
            tag2.text = course.tag2
            tag3.text = course.tag3
            startDate.text = course.startDate
            endDate.text = course.endDate
            lectureCount.text = course.lectureCount
            quizCount.text = course.quizCount
            originalPrice.text = course.originalPrice
            discountPrice.text = course.discountPrice
        }
    }
}
