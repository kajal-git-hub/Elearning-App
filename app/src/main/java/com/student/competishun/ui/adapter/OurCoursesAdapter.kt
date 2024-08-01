package com.student.competishun.ui.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.curator.GetAllCourseCategoriesQuery
import com.student.competishun.data.model.OurCoursesItem
import com.student.competishun.utils.OnCourseItemClickListener

class OurCoursesAdapter(private val listOurCoursesItem: List<GetAllCourseCategoriesQuery.GetAllCourseCategory>,
                        private val itemClickListener: OnCourseItemClickListener
): RecyclerView.Adapter<OurCoursesAdapter.OurCourseViewHolder>() {

    private val images = listOf(
        R.drawable.courseimages_bg,
        R.drawable.student_ic,
        R.drawable.student_mob_ic,
        R.drawable.study_student_ic,
        R.drawable.girl_study_ic,
        R.drawable.book_study_ic
    )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OurCourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.our_courses_item, parent, false)
        return OurCourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: OurCourseViewHolder, position: Int) {
        val itemOurCourse = listOurCoursesItem[position]
        holder.tvCourseName.text = itemOurCourse.name
        val imageResource = images[position % images.size]
        holder.fullYearCourseImage.setImageResource(imageResource)
        holder.itemView.setOnClickListener {
            itemClickListener.onCourseItemClick(itemOurCourse)
        }
    }

    override fun getItemCount(): Int {
        return listOurCoursesItem.size
    }

    class OurCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCourseName: TextView =itemView.findViewById(R.id.tvCourseName)
        val fullYearCourseImage: ImageView = itemView.findViewById(R.id.fullYearCourseImage)
    }

}
