package xyz.penpencil.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.student.competishun.curator.AllCourseForStudentQuery
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.ui.viewmodel.GetCourseByIDViewModel

class StudyMaterialAdapter(private val itemStudyMaterial:  List<AllCourseForStudentQuery.Course>,  private val getCourseByIDViewModel: GetCourseByIDViewModel ) :
    RecyclerView.Adapter<StudyMaterialAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_study_material, parent, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: StudyMaterialAdapter.ViewHolder, position: Int) {
        val courseItem = itemStudyMaterial[position]
         holder.bind(courseItem)
        // subfolderDurationFolders?.let { holder.bind(courseItem, it) }
        getcouseById(courseItem.id, holder)
        holder.itemView.setOnClickListener {
          //  onItemClick(courseItem)
        }
    }

    override fun getItemCount(): Int {
        return itemStudyMaterial.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var iconImageView: ImageView = itemView.findViewById(R.id.cl_course_book)
        private var titleTextView: TextView = itemView.findViewById(R.id.tv_topic_name)
         var noItemTextView: TextView = itemView.findViewById(R.id.mt_no_item)

        fun bind(item: AllCourseForStudentQuery.Course) {
            Log.d("StudyMaterialAdapter", "Binding item: ${item.name}")
            Glide.with(itemView.context)
                .load(item.banner_image)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(iconImageView)
            titleTextView.text = item.name
       //     noItemTextView.text = item.id

        }
    }
   fun getcouseById(courseId: String,  holder: ViewHolder) {

       getCourseByIDViewModel.fetchCourseById(courseId)
       getCourseByIDViewModel.courseByID.observeForever { courses ->
        courses?.folder?.forEach {
            holder.noItemTextView.text = it.pdf_count.toString()
        }
       }
   }
}