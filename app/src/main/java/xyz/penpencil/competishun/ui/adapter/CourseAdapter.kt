package xyz.penpencil.competishun.ui.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.student.competishun.curator.AllCourseForStudentQuery
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.ItemCourseBinding
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.StudentCourseItemClickListener
import java.util.ArrayList

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

            val courseTags = item.course_tags

            val courseBundle = Bundle()
            courseBundle.putStringArrayList("courseTags", courseTags as ArrayList<String>?)


            tvRecommendedCourseName.text = item.name
            tvTag1.apply {
                text = courseTags?.getOrNull(0) ?: ""
                visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
            }
//            tvTag1.text = helperFunctions.toDisplayString(item.course_class?.name) + " Class"
            Log.e("courseclassval",helperFunctions.toDisplayString(item.course_class?.name))// Placeholder, change as needed
            orgPrice.text = "₹${item.price}"
            Log.e("cousediscount ${item.with_installment_price}",item.discount.toString()+ " " + item.price)
            tvStartDate.text = "Starts On: ${helperFunctions.formatCourseDate(item.course_start_date.toString())}"
            tvEndDate.text = "Expiry Date: ${helperFunctions.formatCourseDate(item.course_end_date.toString())}"
            tvQuizTests.text = "validity ${helperFunctions.formatCourseDate(item.course_validity_end_date.toString())}"
            tvLectureNo.text = "Lectures: ${(lectureCounts[item.id] ?: 0)}"
            Glide.with(holder.itemView.context)
                .load(item.banner_image)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.rectangle_1072)
                .error(R.drawable.default_image)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(ivImage)
            if (item.price != null && item.discount != null) {
                val discountDetails = helperFunctions.calculateDiscountPercentage(item.price.toInt(), item.discount.toInt())
                clPercentOffInner.visibility = View.VISIBLE
                dicountPrice.text = "₹${item.discount}"
                discPer.text = "${discountDetails.toInt()}% OFF"
            } else {
                clPercentOffInner.visibility = View.GONE
                dicountPrice.text = "₹${item.price}"
                orgPrice.visibility = View.GONE
                discPer.text = "0% OFF"
            }
            tvTag2.apply {
                text = courseTags?.getOrNull(1) ?: ""
                visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
            }
//            tvTag2.text = item.category_name?.split(" ")?.firstOrNull() ?: ""
            tvTag3.text = "Target ${item.target_year}"

            tvTag4.apply {
                text = courseTags?.getOrNull(2) ?: ""
                visibility = if (text.isEmpty()) View.GONE else View.VISIBLE
            }
            itemCourse.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("course_id", item.id)
                    putStringArrayList("course_tags", item.course_tags as ArrayList<String>?)
                }
                listener.onCourseItemClicked(item, bundle)
            }

        }
    }

    fun updateCourses(newCourses: List<AllCourseForStudentQuery.Course>) {
        items = newCourses
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size
}
