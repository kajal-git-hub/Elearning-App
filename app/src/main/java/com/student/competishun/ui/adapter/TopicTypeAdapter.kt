package com.student.competishun.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.TopicTypeModel
import com.student.competishun.databinding.ItemCourseTypeBinding
import com.student.competishun.databinding.ItemTSizeBinding

class TopicTypeAdapter(private val topicTypeList: List<TopicTypeModel>) : RecyclerView.Adapter<TopicTypeAdapter.TopicTypeViewHolder>() {

    inner class TopicTypeViewHolder(private val binding: ItemCourseTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(type: TopicTypeModel) {
            binding.radioButtonCourseType.text = type.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicTypeViewHolder {
        val binding = ItemCourseTypeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TopicTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicTypeViewHolder, position: Int) {
        holder.bind(topicTypeList[position])
    }


    override fun getItemCount(): Int {
        return topicTypeList.size
    }


}
