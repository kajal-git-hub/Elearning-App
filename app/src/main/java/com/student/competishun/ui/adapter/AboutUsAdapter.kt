package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R

class AboutUsAdapter(private val items: List<AboutUsItem>) : RecyclerView.Adapter<AboutUsAdapter.AboutUsViewHolder>() {

    // ViewHolder class to hold references to the views
    class AboutUsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.igTeacher_bg)
        val teacherName: TextView = itemView.findViewById(R.id.etTeacher_name)
        val teacherSub: TextView = itemView.findViewById(R.id.et_teacher_subject)
        val teacherYear: TextView = itemView.findViewById(R.id.tv_exp_year)
        val courseAndCollege: TextView = itemView.findViewById(R.id.tv_CourseAndCollege)
        val mentorDescRecyclerView: RecyclerView = itemView.findViewById(R.id.rv_mentorsdesc)

    }

    // Inflate the item layout and return a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutUsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mentors_item, parent, false)
        return AboutUsViewHolder(view)
    }

    // Bind data to the views in the ViewHolder
    override fun onBindViewHolder(holder: AboutUsViewHolder, position: Int) {
        val item = items[position]
        holder.image.setBackgroundResource(R.drawable.iv_mentor)
        holder.teacherName.text = item.teacherName
        holder.teacherSub.text = item.teacherSub
        holder.teacherYear.text = item.teacherYear
        holder.courseAndCollege.text = item.courseAndCollege

        val mentorDescAdapter = MentorDescAdapter(item.mentorDescriptions)
        holder.mentorDescRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = mentorDescAdapter
        }
    }

    // Return the total number of items in the list
    override fun getItemCount(): Int = items.size
}

// Data class representing each item
data class AboutUsItem(
    val image:Int,
    val teacherName:String,
    val teacherSub:String,
    val teacherYear:String,
    val courseAndCollege:String,
    val mentorDescriptions: List<String>

)
