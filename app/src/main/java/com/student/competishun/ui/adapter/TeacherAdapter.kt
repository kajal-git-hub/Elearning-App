package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.TeacherItem


class TeacherAdapter(private val items: List<TeacherItem>) :
    RecyclerView.Adapter<TeacherAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var teacherImage: ImageView = itemView.findViewById(R.id.igTeacher_bg)
        private var teacherMame: TextView = itemView.findViewById(R.id.etTeacher_name)
        private var teacherSubject: TextView = itemView.findViewById(R.id.et_teacher_subject)


        fun bind(teacherItem: TeacherItem) {
            teacherMame.text = teacherItem.teacherName
            teacherSubject.text = teacherItem.teacherSubject
            teacherImage.setImageResource(teacherItem.teacherImage)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.teacher_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val courseItem = items[position]
        holder.bind(courseItem)
    }

    override fun getItemCount(): Int = items.size
}