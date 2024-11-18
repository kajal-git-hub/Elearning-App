package xyz.penpencil.competishun.ui.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.student.competishun.curator.AllCourseForStudentQuery
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.ui.viewmodel.GetCourseByIDViewModel
import xyz.penpencil.competishun.utils.StudentCourseItemClickListener
import java.util.ArrayList
import java.util.Locale

class YTCourseAdapter(private var itemYTMaterial:  List<AllCourseForStudentQuery.Course>,private val getCourseByIDViewModel: GetCourseByIDViewModel,  private val listener: StudentCourseItemClickListener) :
    RecyclerView.Adapter<YTCourseAdapter.ViewHolder>(), Filterable {
    private var filteredItems: MutableList<AllCourseForStudentQuery.Course> = itemYTMaterial.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_yt_course, parent, false)
        return ViewHolder(view)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase(Locale.getDefault()) ?: ""

                val filteredList = if (query.isEmpty()) {
                    itemYTMaterial
                } else {
                    itemYTMaterial.filter {
                        it.name.lowercase(Locale.getDefault()).contains(query)
                    }
                    Log.e("youtubveitem",itemYTMaterial.toString())
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems.clear()
                if (results?.values is List<*>) {
                    filteredItems.addAll(results.values as List<AllCourseForStudentQuery.Course>)
                }
                notifyDataSetChanged()
            }
        }
    }



    override fun onBindViewHolder(holder: YTCourseAdapter.ViewHolder, position: Int) {
        val courseItem = itemYTMaterial[position]
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
    fun updateData(newItems: List<AllCourseForStudentQuery.Course>) {
        itemYTMaterial = newItems
        filteredItems.clear()
        filteredItems.addAll(newItems)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return itemYTMaterial.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var iconImageView: ImageView = itemView.findViewById(R.id.cl_thirdLayer)
        private var titleTextView: TextView = itemView.findViewById(R.id.tittle_tv)
      //  var noItemTextView: TextView = itemView.findViewById(R.id.mt_no_item)

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

//        getCourseByIDViewModel.fetchCourseById(courseId)
//        getCourseByIDViewModel.courseByID.observeForever { courses ->
//            courses?.let {
//                val totalPdfCount = courses.folder?.sumOf { folder ->
//                    folder.pdf_count?.toIntOrNull() ?: 0
//                } ?: 0
//                holder.noItemTextView.text = totalPdfCount.toString() + " PDFs"
//            }
//        }
    }
}
