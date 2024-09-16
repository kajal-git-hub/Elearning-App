package com.student.competishun.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.TopicContentModel
import com.student.competishun.R
import com.student.competishun.ui.main.PdfViewerActivity

class DownloadedItemAdapter(private val context: Context, private val items: List<TopicContentModel>) : RecyclerView.Adapter<DownloadedItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clCourseBook : ConstraintLayout = itemView.findViewById(R.id.cl_course_book)
        val ivSubjectBookIcon : ImageView = itemView.findViewById(R.id.iv_subject_book_icon)
        val ivBookShadow : ImageView = itemView.findViewById(R.id.iv_book_Shadow)
        val studyMaterial: TextView = itemView.findViewById(R.id.tv_StudyMaterial)
        val lecTime: TextView = itemView.findViewById(R.id.tv_lecturer_time)
        val topicName: TextView = itemView.findViewById(R.id.tv_topic_name)
        val topicDescription: TextView = itemView.findViewById(R.id.tv_topic_description)
        var forRead : ImageView  = itemView.findViewById(R.id.iv_read_pdf)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.downloads_item_pdfs, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.studyMaterial.text = item.lecture
        if(item.fileType == "PDF"){
            holder.lecTime.text = item.lecturerName
            holder.lecTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.download_person, 0, 0, 0);
            holder.clCourseBook.setBackgroundResource(R.drawable.frame_1707480918)
            holder.ivSubjectBookIcon.setImageResource(R.drawable.group_1707478995)
            holder.ivBookShadow.setImageResource(R.drawable.ellipse_17956)
            holder.forRead.setImageResource(R.drawable.frame_1707481707_1_)
        }else{
            holder.lecTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.clock_black, 0, 0, 0);
            holder.lecTime.text = item.videoDuration.toString()
            holder.forRead.setImageResource(R.drawable.video_bg)
            holder.clCourseBook.setBackgroundResource(R.drawable.frame_1707480918)
            holder.ivSubjectBookIcon.setImageResource(R.drawable.group_1707478994)
            holder.ivBookShadow.setImageResource(R.drawable.ellipse_17956)

        }
        holder.topicName.text = item.topicName
        holder.topicDescription.text = item.topicDescription

        holder.forRead.setOnClickListener {
            val intent = Intent(context, PdfViewerActivity::class.java).apply {
                putExtra("PDF_URL", item.url)
            }
            context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int {
        return items.size
    }
}
