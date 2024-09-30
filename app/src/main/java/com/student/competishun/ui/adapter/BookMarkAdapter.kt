package com.student.competishun.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.TopicContentModel
import com.student.competishun.ui.adapter.DownloadedItemAdapter.OnVideoClickListener
import com.student.competishun.ui.fragment.BottomSheetBookmarkDeleteDownload
import com.student.competishun.ui.main.PdfViewerActivity
import java.io.File

class BookMarkAdapter(
    private val context: Context,
    private val items: MutableList<TopicContentModel>,
    private val fragmentManager: FragmentManager,
    private val videoClickListener: OnVideoClickListener

    ) : RecyclerView.Adapter<BookMarkAdapter.ViewHolder>() {

    interface OnVideoClickListener {
        fun onVideoClick(folderContentId: String, name: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bookmark_item_pdfs, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.studyMaterial.text = item.lecture



        if (item.fileType == "PDF") {
            holder.lecTime.text = item.lecturerName
            holder.lecTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.download_person, 0, 0, 0)
            holder.clCourseBook.setBackgroundResource(R.drawable.frame_1707480918)
            holder.ivSubjectBookIcon.setImageResource(R.drawable.group_1707478995)
            holder.ivBookShadow.setImageResource(R.drawable.ellipse_17956)
            holder.forRead.setImageResource(R.drawable.frame_1707481707_1_)

            holder.dotExtraInfoDownload.setOnClickListener {
                val bottomSheet = BottomSheetBookmarkDeleteDownload()
//                bottomSheet.setListener(this, position,item)
                bottomSheet.show(fragmentManager, bottomSheet.tag)
            }
        } else {
            holder.lecTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.clock_black, 0, 0, 0)
            holder.lecTime.text = formatTimeDuration(item.videoDuration)
            holder.forRead.visibility = View.GONE
            holder.forVideo.visibility = View.VISIBLE
            holder.clCourseBook.setBackgroundResource(R.drawable.frame_1707480918)
            holder.ivSubjectBookIcon.setImageResource(R.drawable.group_1707478994)
            holder.ivBookShadow.setImageResource(R.drawable.ellipse_17956)

            holder.dotExtraInfoDownload.setOnClickListener {
                val bottomSheet = BottomSheetBookmarkDeleteDownload()
//                bottomSheet.setListener(this, position,item)
                bottomSheet.show(fragmentManager, bottomSheet.tag)
            }
        }

        holder.lecTime.text = item.lecturerName
        holder.topicName.text = item.topicName
        holder.topicDescription.text = item.topicDescription

        holder.forRead.setOnClickListener {
            val intent = Intent(context, PdfViewerActivity::class.java)
            intent.putExtra("PDF_URL", item.url)
            context.startActivity(intent)
        }
        holder.forVideo.setOnClickListener {
            videoClickListener.onVideoClick(item.id, item.topicName)

        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clCourseBook: ConstraintLayout = itemView.findViewById(R.id.cl_bm_course_book)
        val ivSubjectBookIcon: ImageView = itemView.findViewById(R.id.iv_bm_subject_book_icon)
        val ivBookShadow: ImageView = itemView.findViewById(R.id.iv_bm_book_Shadow)
        val studyMaterial: TextView = itemView.findViewById(R.id.tv_bm_StudyMaterial)
        val lecTime: TextView = itemView.findViewById(R.id.tv_bm_lecturer_time)
        val topicName: TextView = itemView.findViewById(R.id.tv_bm_topic_name)
        val topicDescription: TextView = itemView.findViewById(R.id.tv_bm_topic_description)
        val forRead: ImageView = itemView.findViewById(R.id.iv_bm_read_pdf)
        val forVideo: ImageView = itemView.findViewById(R.id.iv_bm_read_video)
        val dotExtraInfoDownload: ImageView = itemView.findViewById(R.id.bookmark_extra)
    }
    private fun formatTimeDuration(totalDuration: Int): String {
        return when {
            totalDuration < 60 -> "${totalDuration} sec"
            totalDuration == 60 -> "1h"
            else -> {
                val hours = totalDuration / 3600
                val minutes = (totalDuration % 3600) / 60
                val seconds = totalDuration % 60
                listOf(
                    if (hours > 0) "${hours} hr${if (hours > 1) "s" else ""}" else "",
                    if (minutes > 0) "${minutes} min${if (minutes > 1) "s" else ""}" else "",
                    if (seconds > 0) "${seconds} sec" else ""
                ).filter { it.isNotEmpty() }.joinToString(" ").trim()
            }
        }
    }
}
