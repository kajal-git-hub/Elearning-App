package com.student.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.curator.FindCourseFolderProgressQuery
import com.student.competishun.data.model.FreeDemoItem

class FreeDemoAdapter(private val demoItemList: List<FreeDemoItem>,
                      private val onItemClick: (FreeDemoItem) -> Unit) :
    RecyclerView.Adapter<FreeDemoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.all_demo_free_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val courseItem = demoItemList[position]
        holder.bind(courseItem)
       // subfolderDurationFolders?.let { holder.bind(courseItem, it) }

        holder.itemView.setOnClickListener {
            onItemClick(courseItem)
        }
    }

    override fun getItemCount(): Int {
        return demoItemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        private var titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private var timeTextView: TextView = itemView.findViewById(R.id.timeTextView)

        fun bind(item: FreeDemoItem) {
            Log.d("FreeDemoAdapter", "Binding item: ${item.titleDemo}")
            iconImageView.setImageResource(item.playIcon)
            titleTextView.text = item.titleDemo
            timeTextView.text = item.timeDemo

        }
    }
}
