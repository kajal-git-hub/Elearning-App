package xyz.penpencil.competishun.ui.adapter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.media3.common.Player
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.student.competishun.curator.AllCourseForStudentQuery
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.CourseFItem
import xyz.penpencil.competishun.data.model.FreeDemoItem
import xyz.penpencil.competishun.ui.viewmodel.GetCourseByIDViewModel
import xyz.penpencil.competishun.utils.StudentCourseItemClickListener
import java.util.ArrayList

class StudyMaterialAdapter(private val itemStudyMaterial:  List<AllCourseForStudentQuery.Course>,  private val getCourseByIDViewModel: GetCourseByIDViewModel,  private val listener: StudentCourseItemClickListener) :
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
            val bundle = Bundle().apply {
                putString("course_id", courseItem.id)
                putStringArrayList("course_tags", courseItem.course_tags as ArrayList<String>?)
            }
            listener.onCourseItemClicked(courseItem, bundle)
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
           courses?.let {
               val totalPdfCount = courses.folder?.sumOf { folder ->
                   folder.pdf_count?.toIntOrNull() ?: 0
               } ?: 0
               holder.noItemTextView.text = totalPdfCount.toString() + " PDFs"
           }
       }
       }
   }
