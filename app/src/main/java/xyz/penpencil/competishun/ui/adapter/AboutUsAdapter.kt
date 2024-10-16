package xyz.penpencil.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.R

class AboutUsAdapter(private val items: List<AboutUsItem>) : RecyclerView.Adapter<AboutUsAdapter.AboutUsViewHolder>() {

    class AboutUsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.igTeacher_bg)
        val teacherName: TextView = itemView.findViewById(R.id.etTeacher_name)
        val teacherSub: TextView = itemView.findViewById(R.id.et_teacher_subject)
        val teacherYear: TextView = itemView.findViewById(R.id.tv_exp_year)
        val courseAndCollege: TextView = itemView.findViewById(R.id.tv_CourseAndCollege)
        val mentorDescRecyclerView: RecyclerView = itemView.findViewById(R.id.rv_mentorsdesc)
        val seeMore: TextView = itemView.findViewById(R.id.tvSeeMoreLess)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutUsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mentors_item, parent, false)
        return AboutUsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AboutUsViewHolder, position: Int) {
        val item = items[position]
        holder.image.setImageResource(item.image)
        holder.teacherName.text = item.teacherName
        holder.teacherSub.text = item.teacherSub
        holder.teacherYear.text = item.teacherYear
        holder.courseAndCollege.text = item.courseAndCollege

        val descriptionsToShow = if (item.isExpanded) item.mentorDescriptions else item.mentorDescriptions.take(2)

        val mentorDescAdapter = MentorDescAdapter(descriptionsToShow)
        holder.mentorDescRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = mentorDescAdapter
        }
        holder.seeMore.text = if (item.isExpanded) "See Less" else "See More"

        holder.seeMore.setOnClickListener {
            item.isExpanded = !item.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = items.size
}

data class AboutUsItem(
    val image:Int,
    val teacherName:String,
    val teacherSub:String,
    val teacherYear:String,
    val courseAndCollege:String,
    val mentorDescriptions: List<String>,
    var isExpanded: Boolean = false

)
