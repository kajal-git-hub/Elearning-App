package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.TabItem
import com.student.competishun.databinding.ItemCourseBinding


class CourseAdapter(private val items: List<TabItem>) :
    RecyclerView.Adapter<CourseAdapter.TabItemViewHolder>() {

    class TabItemViewHolder(val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCourseBinding.inflate(inflater, parent, false)
        return TabItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseAdapter.TabItemViewHolder, position: Int) {
        val item = items[position]

        holder.binding.tvRecommendedCourseName.text = "Prakhar Integrated (Fast \nLane-2) 2024-25"
        holder.binding.tvTag1.text = "12th Class"
    }


    override fun getItemCount(): Int = items.size
}


