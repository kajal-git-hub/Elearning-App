package com.student.competishun.ui.adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.TopicTypeModel
import com.student.competishun.databinding.ItemCourseTypeBinding
import com.student.competishun.databinding.ItemTSizeBinding


class TopicTypeAdapter(
    private val topicTypeList: List<TopicTypeModel>,
    private val onItemClicked: (TopicTypeModel) -> Unit // Pass the entire model
) : RecyclerView.Adapter<TopicTypeAdapter.TopicTypeViewHolder>() {

    private var selectedItem: String? = null // Track the selected item

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicTypeViewHolder {
        val binding = ItemCourseTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicTypeViewHolder, position: Int) {
        val topicTypeModel = topicTypeList[position]
        holder.bind(topicTypeModel, selectedItem, onItemClicked)
    }

    override fun getItemCount(): Int {
        return topicTypeList.size
    }

    inner class TopicTypeViewHolder(private val binding: ItemCourseTypeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            topicTypeModel: TopicTypeModel,
            selectedItem: String?,
            onItemClicked: (TopicTypeModel) -> Unit
        ) {
            // Set the radio button text to folder name
            binding.radioButtonCourseType.text = topicTypeModel.title

            // Highlight the selected item
            binding.radioButtonCourseType.isChecked = topicTypeModel.title == selectedItem

            // Set click listener on the radio button or root
            binding.radioButtonCourseType.setOnClickListener {
                this@TopicTypeAdapter.selectedItem = topicTypeModel.title // Update selected item
                onItemClicked(topicTypeModel) // Pass the entire model when clicked
                notifyDataSetChanged() // Notify adapter to refresh the view
            }

            // Optionally display folderId and folderCount in other views if needed
            // For example:
//            binding.tvFolderId.text = topicTypeModel.id
//            binding.tvFolderCount.text = "Count: ${topicTypeModel.count}"
        }
    }
}
