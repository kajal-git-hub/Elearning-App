package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.SubjectContentItem
import com.student.competishun.databinding.ItemCourseContentBinding

class SubjectContentAdapter(
    private val items: List<SubjectContentItem>,
    private val onItemClicked: (SubjectContentItem) -> Unit
) : RecyclerView.Adapter<SubjectContentAdapter.SubjectContentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectContentViewHolder {
        val binding =
            ItemCourseContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectContentViewHolder, position: Int) {
        val course = items[position]
        holder.bind(course)
        holder.itemView.setOnClickListener {
            onItemClicked(course)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class SubjectContentViewHolder(private val binding: ItemCourseContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SubjectContentItem) {
            binding.tvChapterNumber.text = String.format("%02d", item.chapterNumber)
            binding.tvTopicName.text = item.topicName
            binding.tvTopicDescription.text = item.topicDescription
        }
    }
}
