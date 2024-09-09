package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.TopicTypeModel
import com.student.competishun.databinding.ItemCourseTypeBinding

class TopicTypeAdapter(
    private val topicTypeList: List<TopicTypeModel>,
    private val preselectedTopic: String?, // Optional preselected topic type
    private val onItemClicked: (TopicTypeModel) -> Unit // Pass the entire model
) : RecyclerView.Adapter<TopicTypeAdapter.TopicTypeViewHolder>() {

    private var selectedPosition = topicTypeList.indexOfFirst { it.title == preselectedTopic }

    inner class TopicTypeViewHolder(private val binding: ItemCourseTypeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(topicTypeModel: TopicTypeModel, isSelected: Boolean) {
            // Set the text for the item
            binding.radioButtonCourseType.text = topicTypeModel.title

            // Update the background based on the selection status
            binding.root.setBackgroundResource(
                if (isSelected) R.drawable.getstarted_itembg_selected else R.drawable.getstarted_itembg_unselected
            )

            // Update the drawable based on the selection status
            val drawableResId = if (isSelected) R.drawable.property_selected else R.drawable.property_default
            val drawable = ContextCompat.getDrawable(binding.root.context, drawableResId)
            binding.radioButtonCourseType.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

            // Handle click events
            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition

                // Refresh the previous and current selected items
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                // Pass the clicked model to the listener
                onItemClicked(topicTypeModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicTypeViewHolder {
        val binding = ItemCourseTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicTypeViewHolder, position: Int) {
        // Bind data to the ViewHolder, passing whether the item is selected
        holder.bind(topicTypeList[position], position == selectedPosition)
    }

    override fun getItemCount(): Int {
        return topicTypeList.size
    }
}
