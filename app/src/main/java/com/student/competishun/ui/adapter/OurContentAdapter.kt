package com.student.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.curator.GetCourseByIdQuery

class OurContentAdapter(
    private var folderItems: List<GetCourseByIdQuery.Folder>,
    private val isItemSize: ObservableField<Boolean>,
    private var listener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onFirstItemClick(folderId: String)
        fun onOtherItemClick(folderId: String)
    }

    companion object {
        private const val VIEW_TYPE_FIRST_ITEM = 0
        private const val VIEW_TYPE_OTHER_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_FIRST_ITEM
            else -> VIEW_TYPE_OTHER_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FIRST_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.our_content_firstitem, parent, false)
                FirstItemViewHolder(view)
            }
            VIEW_TYPE_OTHER_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.our_content_item, parent, false)
                OtherItemViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val folderItem = folderItems[position]
        when (holder) {
            is FirstItemViewHolder -> holder.bind(folderItem, listener)
            is OtherItemViewHolder -> holder.bind(folderItem, listener)
        }
    }

    override fun getItemCount(): Int {
        return if (isItemSize.get() == true) {
            minOf(folderItems.size, 3)

        } else {
            folderItems.size
        }
    }

    fun updateItems(newFolderItems: List<GetCourseByIdQuery.Folder>) {
        folderItems = newFolderItems
        notifyDataSetChanged()
    }

    class FirstItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        private val freeBadgeImageView: ImageView = itemView.findViewById(R.id.freeBadgeImageView)

        fun bind(item: GetCourseByIdQuery.Folder, listener: OnItemClickListener) {

            titleTextView.text = item.name
            if (item.name.startsWith("Free")) {
                iconImageView.setImageResource(R.drawable.frame_1707480918)
                freeBadgeImageView.setImageResource(R.drawable.group_1272628768)
            } else {
                iconImageView.visibility = View.GONE
                freeBadgeImageView.visibility = View.GONE
            }

            itemView.setOnClickListener {
                listener.onFirstItemClick(item.id)
            }
        }
    }

    class OtherItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        private val lockImage: ImageView = itemView.findViewById(R.id.ig_lockImage)

        fun bind(item: GetCourseByIdQuery.Folder, listener: OnItemClickListener) {
            titleTextView.text = item.name
            iconImageView.setImageResource(R.drawable.frame_1707480918)
            lockImage.setImageResource(R.drawable.lock)

            itemView.setOnClickListener {
                listener.onOtherItemClick(item.id)
            }
        }
    }
}

