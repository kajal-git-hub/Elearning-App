package xyz.penpencil.competishun.ui.adapter

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.TeacherItem

class TeacherAdapter(private val items: List<TeacherItem>) :
    RecyclerView.Adapter<TeacherAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var teacherImage: ImageView = itemView.findViewById(R.id.igTeacher_bg)
        private var teacherName: TextView = itemView.findViewById(R.id.etTeacher_name)
        private var teacherSubject: TextView = itemView.findViewById(R.id.et_teacher_subject)

        fun bind(teacherItem: TeacherItem) {
            teacherName.text = teacherItem.teacherName
            teacherSubject.text = teacherItem.teacherSubject
            teacherImage.setImageResource(teacherItem.teacherImage)

            itemView.setOnClickListener {
                showTeacherDetailsDialog(itemView.context, teacherItem)
            }
        }

        private fun showTeacherDetailsDialog(context: android.content.Context, teacherItem: TeacherItem) {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_teacher_details)

            val teacherName: TextView = dialog.findViewById(R.id.tvTeacherName)
            val teacherSubject: TextView = dialog.findViewById(R.id.tvTeacherSubject)
            val mentorDescriptionLayout: TextView = dialog.findViewById(R.id.layoutMentorDescriptions)

            teacherName.text = teacherItem.teacherName
            teacherSubject.text = teacherItem.teacherSubject
            mentorDescriptionLayout.text = teacherItem.mentorDescriptions

            dialog.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.teacher_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val teacherItem = items[position]
        holder.bind(teacherItem)
    }

    override fun getItemCount(): Int = items.size
}
