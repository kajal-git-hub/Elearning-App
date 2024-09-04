package com.student.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.databinding.ItemCourseBinding
import com.student.competishun.utils.HelperFunctions
import com.student.competishun.utils.StudentCourseItemClickListener

class CourseAdapter(
    private var items: List<AllCourseForStudentQuery.Course>,
    private val lectureCounts: Map<String, Int>,
    private val listener: StudentCourseItemClickListener
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private lateinit var helperFunctions: HelperFunctions

    class CourseViewHolder(val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCourseBinding.inflate(inflater, parent, false)
        helperFunctions = HelperFunctions()
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val item = items[position]

        holder.binding.apply {
            tvRecommendedCourseName.text = item.name
            tvTag1.text = helperFunctions.toDisplayString(item.course_class?.name) + " Class"
            Log.e("courseclassval",helperFunctions.toDisplayString(item.course_class?.name))// Placeholder, change as needed
            orgPrice.text = "₹${item.price}"
            Log.e("cousediscount ${item.with_installment_price}",item.discount.toString()+ " " + item.price)
            tvStartDate.text = "Starts On: ${helperFunctions.formatCourseDate(item.course_start_date.toString())}"
            tvEndDate.text = "Expiry Date: ${helperFunctions.formatCourseDate(item.course_end_date.toString())}"
            tvQuizTests.text = "validity ${helperFunctions.formatCourseDate(item.course_validity_end_date.toString())}"
            tvLectureNo.text = "Lectures: ${(lectureCounts[item.id] ?: 0)}"
            Glide.with(holder.itemView.context)

                .load(item.banner_image)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(ivImage)
            if (item.price != null && item.discount != null) {
                val discountDetails = helperFunctions.calculateDiscountPercentage(item.price.toInt(), item.discount.toInt())
                dicountPrice.text = "₹${item.discount}"
                discPer.text = "${discountDetails.toInt()}% OFF"
            } else {
                dicountPrice.text = "₹0"
                discPer.text = "0% OFF"
            }

            tvTag2.text = item.category_name?.split(" ")?.firstOrNull() ?: ""
            tvTag3.text = "Target ${item.target_year}"

            itemCourse.setOnClickListener {
                listener.onCourseItemClicked(item)
            }
        }
    }

    fun updateCourses(newCourses: List<AllCourseForStudentQuery.Course>) {
        items = newCourses
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}
