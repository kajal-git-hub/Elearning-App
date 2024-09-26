package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import okhttp3.internal.ignoreIoExceptions

class MentorDescAdapter(private val mentorDescriptions: List<String>) :
    RecyclerView.Adapter<MentorDescAdapter.MentorDescViewHolder>() {

    // ViewHolder for the inner RecyclerView
    class MentorDescViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image : ImageView = itemView.findViewById(R.id.iv_dot)
        val descriptionText: TextView = itemView.findViewById(R.id.tv_mentor_desc)
    }

    // Inflate the item layout for the inner RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentorDescViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mentors_desc, parent, false)
        return MentorDescViewHolder(view)
    }

    // Bind the data to the inner RecyclerView's views
    override fun onBindViewHolder(holder: MentorDescViewHolder, position: Int) {
        holder.image.setBackgroundResource(R.drawable.dot)
        holder.descriptionText.text = mentorDescriptions[position]
    }

    // Return the size of the list for the inner RecyclerView
    override fun getItemCount(): Int = mentorDescriptions.size
}
