package com.student.competishun.ui.adapter.test

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R

class TestStatusAdapter(private val items: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_TITLE = 0
        private const val TYPE_ITEM = 1
    }

    // ViewHolder for Title
    class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
    }

    // ViewHolder for Grid Item
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gridImageView: ImageView = itemView.findViewById(R.id.grid)
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) {
            TYPE_TITLE
        } else {
            TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_TITLE) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_test_status_title, parent, false)
            TitleViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_test_status_grid, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_TITLE) {
            (holder as TitleViewHolder).titleTextView.text = items[position] as String
        } else {
            val item = items[position] as GridItem
            (holder as ItemViewHolder).gridImageView.setImageResource(item.imageResId)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    data class GridItem(val imageResId: Int)

}
