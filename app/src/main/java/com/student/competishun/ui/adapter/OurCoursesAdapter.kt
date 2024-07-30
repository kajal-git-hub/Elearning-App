package com.student.competishun.ui.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.curator.GetAllCourseCategoriesQuery
import com.student.competishun.data.model.OurCoursesItem

class OurCoursesAdapter(private val listOurCoursesItem: List<GetAllCourseCategoriesQuery.GetAllCourseCategory>): RecyclerView.Adapter<OurCoursesAdapter.OurCourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OurCourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.our_courses_item, parent, false)
        return OurCourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: OurCourseViewHolder, position: Int) {
        val itemOurCourse = listOurCoursesItem[position]
        holder.tvCourseName.text = itemOurCourse.name
    }

    override fun getItemCount(): Int {
        return listOurCoursesItem.size
    }

    class OurCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCourseName: TextView =itemView.findViewById(R.id.tvCourseName)
    }

}
