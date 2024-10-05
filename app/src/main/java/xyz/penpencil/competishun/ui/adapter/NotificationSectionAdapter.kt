package xyz.penpencil.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.NotificationSection

class NotificationSectionAdapter(private val sections: List<NotificationSection>) :
    RecyclerView.Adapter<NotificationSectionAdapter.SectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification_section, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        holder.bind(section)
    }

    override fun getItemCount(): Int = sections.size

    inner class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val sectionTitle: TextView = view.findViewById(R.id.tvSectionTitle)
        private val recyclerView: RecyclerView = view.findViewById(R.id.rvNotifications)

        fun bind(section: NotificationSection) {
            sectionTitle.text = section.sectionTitle
            val adapter = NotificationAdapter(section.notifications)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(itemView.context)
        }
    }
}
