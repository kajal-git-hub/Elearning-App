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
import com.student.competishun.data.model.OtherContentItem
import com.student.competishun.data.model.OurContentFirstItem
import com.student.competishun.data.model.OurContentItem

class OurContentAdapter(
    private var items: List<OurContentItem>,
    private val isItemSize: ObservableField<Boolean>,
    private var listener: OnItemClickListener

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onFirstItemClick()
    }

    companion object {
        private const val VIEW_TYPE_FIRST_ITEM = 0
        private const val VIEW_TYPE_OTHER_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is OurContentItem.FirstItem -> VIEW_TYPE_FIRST_ITEM
            is OurContentItem.OtherItem -> VIEW_TYPE_OTHER_ITEM
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
        Log.d("ContentAdapter", "view position $position with item ${items[position]}")
        when (val item = items[position]) {
            is OurContentItem.FirstItem -> (holder as FirstItemViewHolder).bind(item.item, listener)
            is OurContentItem.OtherItem -> (holder as OtherItemViewHolder).bind(item.item)
        }
    }

    override fun getItemCount(): Int {
        return if (isItemSize.get() == true) {
            items.size
        } else {
            items.size
        }
    }

    fun updateItems(newItems: List<OurContentItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    class FirstItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        private val freeBadgeImageView: ImageView = itemView.findViewById(R.id.freeBadgeImageView)

        fun bind(item: OurContentFirstItem, listener: OnItemClickListener) {
            titleTextView.text = item.title
            iconImageView.setImageResource(item.iconResId)
            freeBadgeImageView.setImageResource(item.isFree)

            itemView.setOnClickListener {
                listener.onFirstItemClick()
            }
        }
    }

    class OtherItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        private val lockImage: ImageView = itemView.findViewById(R.id.ig_lockImage)

        fun bind(item: OtherContentItem) {
            titleTextView.text = item.title2
            iconImageView.setImageResource(item.iconResId2)
            lockImage.setImageResource(item.lock)
        }
    }
}
