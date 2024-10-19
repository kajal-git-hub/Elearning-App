package xyz.penpencil.competishun.ui.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.curator.GetAllCourseCategoriesQuery
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.utils.OnCourseItemClickListener

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
        Log.e("coursesname",itemOurCourse.name)
        if (itemOurCourse.name == "Study Material") {
            holder.itemView.visibility = View.GONE
        }else holder.itemView.visibility = View.VISIBLE

        if (itemOurCourse.name == "Digital Book" || itemOurCourse.name == "Chapter Wise Test"){
            holder.fullYearComing.visibility = View.VISIBLE
        } else { holder.fullYearComing.visibility = View.GONE }
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
        val fullYearComing: ImageView = itemView.findViewById(R.id.fullYearcoming)
    }

}
