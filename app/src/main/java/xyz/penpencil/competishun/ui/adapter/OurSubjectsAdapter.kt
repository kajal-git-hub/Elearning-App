package xyz.penpencil.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.student.competishun.curator.FindCourseParentFolderProgressQuery
import xyz.penpencil.competishun.R

class OurSubjectsAdapter(private val listOurSubjectItem:List<FindCourseParentFolderProgressQuery. Folder?>, private val progress: List<Double>, private val onItemClicked: (String, String, String ) -> Unit
): RecyclerView.Adapter<OurSubjectsAdapter.OurSubjectViewHolder>() {
    private val images = listOf(
        R.drawable.layer_1__1_,
        R.drawable.group__4_,
        R.drawable.capa_1,
        R.drawable.group__5_,
        R.drawable.layer_1,
        R.drawable.biology_icon,
        R.drawable.layer_1__1_
    )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OurSubjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.our_subjects_item, parent, false)
        return OurSubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: OurSubjectViewHolder, position: Int) {
        val itemOurSubject = listOurSubjectItem[position]
        val duration = progress[position] as Double
        holder.tvSubjectName.text = itemOurSubject?.name?:""
        if (holder.tvSubjectName.text.contains("physics", ignoreCase = true)){
            holder.ivMathematics.setImageResource(images[1])
            holder.customProgressIndicatorMathematics.visibility = View.VISIBLE
            holder.tvPercentCompletedMathematics.visibility = View.VISIBLE
        }else if (holder.tvSubjectName.text.contains("Chemistry")){
            holder.ivMathematics.setImageResource(images[2])
            holder.customProgressIndicatorMathematics.visibility = View.VISIBLE
            holder.tvPercentCompletedMathematics.visibility = View.VISIBLE
        }
        else if (holder.tvSubjectName.text.contains("Biology", ignoreCase = true)){
            holder.ivMathematics.setImageResource(images[5])
            holder.customProgressIndicatorMathematics.visibility = View.VISIBLE
            holder.tvPercentCompletedMathematics.visibility = View.VISIBLE
        }
        else if (holder.tvSubjectName.text.contains("Text", ignoreCase = true)){
            holder.ivMathematics.setImageResource(images[6])
            holder.customProgressIndicatorMathematics.visibility = View.GONE
            holder.tvPercentCompletedMathematics.visibility = View.GONE
        }
        else if (holder.tvSubjectName.text.contains("Other", ignoreCase = true)){
            holder.ivMathematics.setImageResource(images[6])
            holder.customProgressIndicatorMathematics.visibility = View.GONE
            holder.tvPercentCompletedMathematics.visibility = View.GONE
        }
        else if (holder.tvSubjectName.text.contains("Class", ignoreCase = true )){
            holder.freeBadgeImageView.visibility = View.VISIBLE
            holder.customProgressIndicatorMathematics.visibility = View.GONE
            holder.tvPercentCompletedMathematics.visibility = View.GONE
            holder.ivMathematics.setImageResource(images[6])
        }else if (holder.tvSubjectName.text.contains("Maths", ignoreCase = true )){
            holder.ivMathematics.setImageResource(images[4])
            holder.customProgressIndicatorMathematics.visibility = View.VISIBLE
            holder.tvPercentCompletedMathematics.visibility = View.VISIBLE
        }
        else if (holder.tvSubjectName.text.contains("Mathematics", ignoreCase = true )){
            holder.ivMathematics.setImageResource(images[4])
            holder.customProgressIndicatorMathematics.visibility = View.VISIBLE
            holder.tvPercentCompletedMathematics.visibility = View.VISIBLE
        }

        else{
            holder.ivMathematics.setImageResource(images[6])
        }
//        val imageResource = images[position % images.size]
//        holder.ivMathematics.setImageResource(imageResource)
        holder.tvNoOfChaptersMathematics.text = itemOurSubject?.folder_count +" Chapters"
        holder.customProgressIndicatorMathematics.progress = duration.toInt()
        holder.tvPercentCompletedMathematics.text = "${duration.toInt()}%"
        holder.itemView.setOnClickListener {
         Log.e("getignite",itemOurSubject.toString())
          onItemClicked(itemOurSubject?.id?:"",itemOurSubject?.name?:"",itemOurSubject?.folder_count.toString())

        }


    }

    override fun getItemCount(): Int {
        return listOurSubjectItem.size
    }

    class OurSubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSubjectName: TextView =itemView.findViewById(R.id.tvMathematics)
        val tvNoOfChaptersMathematics: TextView = itemView.findViewById(R.id.tvNoOfChaptersMathematics)
         val ivMathematics: ImageView = itemView.findViewById(R.id.ivMathematics)
        val freeBadgeImageView: ImageView = itemView.findViewById(R.id.freeBadgeImageView)
        val customProgressIndicatorMathematics: ProgressBar = itemView.findViewById(R.id.customProgressIndicatorMathematics)
        val tvPercentCompletedMathematics: MaterialTextView = itemView.findViewById(R.id.tvPercentCompletedMathematics)
    }

}
