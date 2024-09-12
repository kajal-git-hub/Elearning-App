package com.student.competishun.ui.adapter

import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.TopicContentModel
import com.student.competishun.databinding.ItemTopicTypeContentBinding
import com.student.competishun.ui.fragment.BottomSheetDownloadBookmark
import com.student.competishun.ui.fragment.BottomSheetTSizeFragment

class TopicContentAdapter(private val topicContents: List<TopicContentModel>, private val folderContentId: String, private val fragmentActivity: FragmentActivity, private val onItemClick: (TopicContentModel, String) -> Unit) :
    RecyclerView.Adapter<TopicContentAdapter.TopicContentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicContentViewHolder {
        val binding = ItemTopicTypeContentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TopicContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicContentViewHolder, position: Int) {

        val topicContent = topicContents[position]
        holder.bind(topicContents[position],fragmentActivity)

        holder.itemView.setOnClickListener {
            onItemClick(topicContent,folderContentId)
        }
    }

    override fun getItemCount(): Int = topicContents.size

    class TopicContentViewHolder(private val binding: ItemTopicTypeContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(topicContent: TopicContentModel, fragmentActivity: FragmentActivity) {
            binding.ivSubjectBookIcon.setImageResource(topicContent.subjectIcon)

            binding.ivMoreInfoLec.setOnClickListener {
                val bottomSheet = BottomSheetDownloadBookmark()
                bottomSheet.show(fragmentActivity.supportFragmentManager, bottomSheet.tag)
            }

            if (topicContent.playIcon != 0) {
                binding.videoicon.setImageResource(topicContent.playIcon)
                binding.videoicon.visibility = View.VISIBLE
                binding.ivPlayVideoIcon.visibility = View.VISIBLE

            } else {
                binding.videoicon.setImageDrawable(null) // or setVisibility(View.GONE) to hide the view
            }

            binding.tvLecture.text = topicContent.lecture
            binding.tvLecturerName.text = topicContent.lecturerName
            binding.tvTopicName.text = topicContent.topicName

            // Handle the topic description with truncation and "Read More"
            binding.tvCourseDescription.post {
                val maxLines = 2
                if (binding.tvCourseDescription.lineCount > maxLines) {
                    val originalText = topicContent.topicDescription
                    val end = binding.tvCourseDescription.layout.getLineEnd(maxLines - 1)
                    val truncatedText = originalText.substring(0, end).trim()

                    if (truncatedText.length < originalText.length) {
                        val spannableString = SpannableString("$truncatedText... Read more")

                        val clickableSpan = object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                // Handle the "Read More" click event
                                binding.tvCourseDescription.text = originalText
                            }
                        }

                        spannableString.setSpan(
                            clickableSpan,
                            spannableString.length - "Read more".length,
                            spannableString.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        // Set the color for "Read More"
                        spannableString.setSpan(
                            ForegroundColorSpan(ContextCompat.getColor(binding.root.context, R.color.blue_3E3EF7)),
                            spannableString.length - "Read more".length,
                            spannableString.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        binding.tvCourseDescription.text = spannableString
                        binding.tvCourseDescription.maxLines = maxLines
                        binding.tvCourseDescription.ellipsize = TextUtils.TruncateAt.END
                    } else {
                        binding.tvCourseDescription.text = originalText
                    }
                } else {
                    binding.tvCourseDescription.text = topicContent.topicDescription
                }
            }

        }
    }
}
