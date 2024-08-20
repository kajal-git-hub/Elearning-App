package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.type.CreateCartItemDto
import com.student.competishun.curator.type.EntityType
import com.student.competishun.data.model.TabItem
import com.student.competishun.databinding.ItemCourseBinding
import com.student.competishun.utils.HelperFunctions
import com.student.competishun.utils.OnCourseItemClickListener
import com.student.competishun.utils.StudentCourseItemClickListener


class CourseAdapter(private val items: List<AllCourseForStudentQuery.Course>,
                    private val listener: StudentCourseItemClickListener
) :
    RecyclerView.Adapter<CourseAdapter.TabItemViewHolder>() {
    private lateinit var helperFunctions: HelperFunctions
    class TabItemViewHolder(val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCourseBinding.inflate(inflater, parent, false)
        helperFunctions = HelperFunctions()
        return TabItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseAdapter.TabItemViewHolder, position: Int) {
        val item = items[position]
        holder.binding.itemCourse.setOnClickListener{
            listener.onCourseItemClicked(item)
        }
        holder.binding.tvRecommendedCourseName.text = item.name
        holder.binding.tvTag1.text = "12th Class"
        holder.binding.orgPrice.text = "₹"+item.price.toString()
        holder.binding.tvStartDate.text = "Starts On: " + helperFunctions.formatCourseDate(item.course_start_date.toString())
        holder.binding.tvEndDate.text = "Expiry Date: " + helperFunctions.formatCourseDate(item.course_validity_end_date.toString())
        if (item.price != null && item.discount !==null ) {
            holder.binding.dicountPrice.text = "₹" + helperFunctions.calculateDiscountDetails(item.price.toDouble(), item.discount.toDouble()).second
            holder.binding.discPer.text = helperFunctions.calculateDiscountDetails(item.price.toDouble(), item.discount.toDouble()).first.toString()+"% OFF"
        }else{
            holder.binding.dicountPrice.text = "0"
            holder.binding.discPer.text = "0"
        }
        holder.binding.tvTag2.text = item.category_name?.split(" ")?.firstOrNull() ?: ""
        holder.binding.tvTag3.text = "Target "+item.target_year.toString()
    }

    fun getSelectedCartItems(selectedIds: String): List<CreateCartItemDto> {
        return items.filter { it.id in selectedIds }.map { course ->
            CreateCartItemDto(
                entity_id = course.id,
                entity_type = EntityType.COURSE,
                quantity = 1
            )
        }
    }


    override fun getItemCount(): Int = items.size
}


