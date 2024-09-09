package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.CourseFItem

class CourseFeaturesAdapter(private val items: List<CourseFItem>) :
    RecyclerView.Adapter<CourseFeaturesAdapter.ViewHolder>() {

    private val images = listOf(R.drawable.group_1272628766, R.drawable.course_feature)


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        var imageViewIcon: ImageView = itemView.findViewById(R.id.imBottomImage)


        fun bind(courseFItem: CourseFItem,imageResId:Int) {
            textViewTitle.text = courseFItem.featureText
            imageViewIcon.setImageResource(imageResId)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val courseItem = items[position]
        val imageResId = images[position%2]
        holder.bind(courseItem,imageResId)
    }

    override fun getItemCount(): Int = items.size
}
