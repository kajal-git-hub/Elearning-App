package xyz.penpencil.competishun.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.ui.fragment.BottomSheetDeletePDFsFragment
import xyz.penpencil.competishun.ui.fragment.BottomSheetDeleteVideoFragment
import xyz.penpencil.competishun.ui.fragment.DownloadFragment
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.ui.main.PdfViewActivity
import xyz.penpencil.competishun.utils.OnDeleteClickListener
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import java.io.File
import java.util.Locale

class DownloadedItemAdapter(
    private val context: Context,
    private val items: MutableList<TopicContentModel>,
    private val videoClickListener: OnVideoClickListener,
    private val fragmentManager: FragmentManager,
    private val fragment: DownloadFragment
) : RecyclerView.Adapter<DownloadedItemAdapter.ViewHolder>(), OnDeleteClickListener, Filterable {

    private var filteredItems: MutableList<TopicContentModel> = items.toMutableList()

    interface OnVideoClickListener {
        fun onVideoClick(topicContentModel: TopicContentModel)
        fun onDeleteClick(topicContentModel: TopicContentModel)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clCourseBook: ConstraintLayout = itemView.findViewById(R.id.cl_course_book)
        val ivSubjectBookIcon: ImageView = itemView.findViewById(R.id.iv_subject_book_icon)
        val ivBookShadow: ImageView = itemView.findViewById(R.id.iv_book_Shadow)
        val studyMaterial: TextView = itemView.findViewById(R.id.tv_StudyMaterial)
        val lecTime: TextView = itemView.findViewById(R.id.tv_lecturer_time)
        val topicName: TextView = itemView.findViewById(R.id.tv_topic_name)
        val topicDescription: TextView = itemView.findViewById(R.id.tv_topic_description)
        var forRead: ImageView = itemView.findViewById(R.id.iv_read_pdf)
        var forVideo: ImageView = itemView.findViewById(R.id.iv_read_video)
        var dotExtraInfoDownload: ImageView = itemView.findViewById(R.id.dotExtraInfoDownload)
    }

    override fun onDeleteClick(position: Int, item: TopicContentModel) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            filteredItems.removeAt(position) // Also remove from filtered list
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
            videoClickListener.onDeleteClick(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.downloads_item_pdfs, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredItems[position]
        holder.studyMaterial.text = item.lecture
        holder.itemView.setOnClickListener {
            videoClickListener.onVideoClick(item)
        }
        if (item.fileType == "PDF") {
            holder.lecTime.text = item.lecturerName
            holder.lecTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.download_person, 0, 0, 0)
            holder.clCourseBook.setBackgroundResource(R.drawable.frame_1707480919)
            holder.ivSubjectBookIcon.setImageResource(R.drawable.group_1707478995)
            holder.ivBookShadow.setImageResource(R.drawable.ellipse_17956)
            holder.forRead.visibility = View.VISIBLE
            holder.forVideo.visibility = View.GONE
            holder.forRead.setImageResource(R.drawable.frame_1707481707_1_)

            holder.dotExtraInfoDownload.setOnClickListener {
                val bottomSheet = BottomSheetDeletePDFsFragment()
                bottomSheet.setListener(this, position, item)
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
            holder.forVideo.setImageResource(R.drawable.frame_1707481707)

            holder.dotExtraInfoDownload.setOnClickListener {
                val bottomSheet = BottomSheetDeleteVideoFragment()
                bottomSheet.setListener(this, position, item)
                bottomSheet.show(fragmentManager, bottomSheet.tag)
            }
        }

        holder.topicName.text = item.topicName
        holder.topicDescription.text = item.topicDescription

        holder.forRead.setOnClickListener {
            val localPath = File(context.filesDir, item.topicName + ".pdf")
            val intent = Intent(context, PdfViewActivity::class.java).apply {
                putExtra("PDF_URL", localPath.absolutePath)
                putExtra("PDF_TITLE", item.topicName)
            }
            context.startActivity(intent)
        }

//        holder.forVideo.setOnClickListener {
//            videoClickListener.onVideoClick(item)
//        }
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = FilterResults()
                val query = constraint.toString().lowercase(Locale.getDefault())

                filteredResults.values = if (query.isEmpty()) {
                    items
                } else {
                    items.filter {
                        it.topicName.lowercase(Locale.getDefault()).contains(query) ||
                                it.lecture.lowercase(Locale.getDefault()).contains(query)
                    }
                }
                return filteredResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results?.values as MutableList<TopicContentModel>
                notifyDataSetChanged()
            }
        }
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

