package xyz.penpencil.competishun.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.ui.fragment.BookMarkFragment
import xyz.penpencil.competishun.ui.fragment.BottomSheetBookmarkDeleteDownload
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.ui.fragment.closeListener
import xyz.penpencil.competishun.ui.main.PdfViewerActivity
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.ui.main.PdfViewActivity
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import java.io.File

class BookMarkAdapter(
    private val context: Context,
    private val items: MutableList<TopicContentModel>,
    private val fragmentManager: FragmentManager,
    private val videoClickListener: OnVideoClickListener,
    private val fragment: BookMarkFragment


) : RecyclerView.Adapter<BookMarkAdapter.ViewHolder>(), BottomSheetBookmarkDeleteDownload.OnDeleteItemListener {

    private var filteredItems: MutableList<TopicContentModel> = items.toMutableList()

    fun updateItems(newItems: List<TopicContentModel>) {
        items.clear()
        items.addAll(newItems)
        filteredItems.clear()
        filteredItems.addAll(newItems)
        notifyDataSetChanged()
    }


    fun filter(query: String?) {
        filteredItems.clear()
        if (query.isNullOrEmpty()) {
            filteredItems.addAll(items)
        } else {
            val filterPattern = query.lowercase().trim()
            filteredItems.addAll(items.filter { item ->
                item.topicName.lowercase().contains(filterPattern) ||
                        item.lecture.lowercase().contains(filterPattern) ||
                        item.topicDescription.lowercase().contains(filterPattern)
            })
        }
        notifyDataSetChanged()
//        fragment.checkEmptyState()
    }

    interface OnVideoClickListener {
        fun onVideoClick(folderContentId: String, name: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bookmark_item_pdfs, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredItems[position]

        var helperFunctions = HelperFunctions()
        holder.studyMaterial.text = item.lecture



        if (item.fileType == "PDF") {
            holder.lecTime.text = item.lecturerName
            holder.forRead.visibility = View.VISIBLE
            holder.forVideo.visibility = View.GONE
            holder.lecTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.download_person, 0, 0, 0)
            holder.clCourseBook.setBackgroundResource(R.drawable.frame_1707480919)
            holder.ivSubjectBookIcon.setImageResource(R.drawable.group_1707478995)
            holder.ivBookShadow.setImageResource(R.drawable.ellipse_17956)
            holder.forRead.visibility = View.VISIBLE
            holder.forVideo.visibility = View.GONE
            holder.etHomeWorkPdf.visibility = View.GONE
            holder.etHomeWorkText.visibility = View.GONE
            holder.forRead.setImageResource(R.drawable.frame_1707481707_1_)

            holder.dotExtraInfoDownload.setOnClickListener {
                val bottomSheet = BottomSheetBookmarkDeleteDownload(this)

                bottomSheet.closeFragment(object : closeListener {
                    override fun close() {
                       bottomSheet.dismiss()
                    }
                })
                bottomSheet.setItem(position, item)
                bottomSheet.show(fragmentManager, bottomSheet.tag)
            }
        } else {
            holder.lecTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.clock_black, 0, 0, 0)
            holder.lecTime.text = formatTimeDuration(item.videoDuration)
            holder.forRead.visibility = View.GONE
            holder.forVideo.visibility = View.VISIBLE
            holder.clCourseBook.setBackgroundResource(R.drawable.frame_1707480919)
            holder.ivSubjectBookIcon.setImageResource(R.drawable.group_1707478994)
            holder.ivBookShadow.setImageResource(R.drawable.ellipse_17956)
            holder.forRead.setImageResource(R.drawable.frame_1707481707)

            holder.etHomeWorkText.visibility = View.VISIBLE
            holder.etHomeWorkPdf.visibility = View.VISIBLE
            holder.etHomeWorkPdf.text = if (item.homeworkName.isNotEmpty()) item.homeworkName else "NA"
            holder.etHomeWorkPdf.setOnClickListener {
                helperFunctions.downloadPdf(context,item.homeworkUrl,item.homeworkName)
            }

            holder.dotExtraInfoDownload.setOnClickListener {
                val bottomSheet = BottomSheetBookmarkDeleteDownload(this)
                bottomSheet.setItem(position, item)
                bottomSheet.show(fragmentManager, bottomSheet.tag)
            }
        }

        holder.lecTime.text = item.lecturerName
        holder.topicName.text = item.topicName
        holder.topicDescription.text = item.topicDescription

        holder.forRead.setOnClickListener {
            val intent = Intent(context, PdfViewActivity::class.java)
            intent.putExtra("PDF_URL", item.url)
            intent.putExtra("PDF_TITLE",item.topicName)
            context.startActivity(intent)
        }

        holder.forVideo.setOnClickListener {
            videoClickListener.onVideoClick(item.id,item.topicName)
        }

    }
    override fun onDeleteItem(position: Int,item: TopicContentModel) {

        val sharedPreferencesManager = SharedPreferencesManager(context)
        sharedPreferencesManager.deleteDownloadedItemBm(item)


        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)


        if (item.fileType=="PDF")
        {
            val fileName = "${item.topicName}.${item.fileType.lowercase()}"
            val file = File(context.filesDir, fileName)
            Log.d("DownloadDelete", file.exists().toString())
            if (file.exists()) {
                file.delete()
            }
        }else
        {
            val fileName = "${item.topicName}.mp4"
            val file = File(context.filesDir, fileName)
            Log.d("DownloadDelete", file.exists().toString())
            if (file.exists()) {
                file.delete()
            }
        }

        fragment.updateDownloadedItems(item.fileType)    }

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
        val etHomeWorkPdf: TextView = itemView.findViewById(R.id.et_homeWorkPdf)
        val etHomeWorkText: TextView = itemView.findViewById(R.id.et_homeWorkText)
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
