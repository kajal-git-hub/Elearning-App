package com.student.competishun.ui.adapter

import com.student.competishun.data.model.TopicContentModel
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.databinding.ItemTopicTypeContentBinding


class TopicContentAdapter(private val topicContents: List<TopicContentModel>) :
    RecyclerView.Adapter<TopicContentAdapter.TopicContentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicContentViewHolder {
        val binding = ItemTopicTypeContentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TopicContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicContentViewHolder, position: Int) {
        holder.bind(topicContents[position])
    }

    override fun getItemCount(): Int = topicContents.size

    class TopicContentViewHolder(private val binding: ItemTopicTypeContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(topicContent: TopicContentModel) {
            binding.ivSubjectBookIcon.setImageResource(topicContent.subjectIcon)
            binding.ivPlayVideoIcon.setImageResource(topicContent.playIcon)
            binding.tvLecture.text = topicContent.lecture
            binding.tvLecturerName.text = topicContent.lecturerName
            binding.tvTopicName.text = topicContent.topicName
            binding.tvTopicDescription.text = topicContent.topicDescription
//            binding.progressBarTopicTypeFragment.progress = topicContent.progress
            binding.tvTopicDescription.post {
                if (binding.tvTopicDescription.lineCount > 2) {
                    binding.tvTopicDescription.maxLines = 2
                    binding.tvTopicDescription.ellipsize = android.text.TextUtils.TruncateAt.END
                    val readMore = " ... Read more"
                    val text = binding.tvTopicDescription.text.toString()
                    binding.tvTopicDescription.text = text + readMore
                }
            }
        }
    }
}