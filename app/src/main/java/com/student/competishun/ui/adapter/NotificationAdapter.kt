package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.student.competishun.R
import com.student.competishun.data.model.NotificationItem

class NotificationAdapter(private val notifications: List<NotificationItem>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification)
    }

    override fun getItemCount(): Int = notifications.size

    inner class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val startUrl : ImageView = view.findViewById(R.id.ivNotificationIcon)
        private val title: TextView = view.findViewById(R.id.tvNotificationTitle)
        private val description: TextView = view.findViewById(R.id.tvNotificationDescription)
        private val image: ImageView = view.findViewById(R.id.ivNotificationImage)

        fun bind(notification: NotificationItem) {
            startUrl.setImageResource(notification.startUrl)
            title.text = notification.title
            description.text = notification.description
            if (notification.imageUrl != null) {
                image.visibility = View.VISIBLE
                Glide.with(itemView.context).load(notification.imageUrl).into(image)
            } else {
                image.visibility = View.GONE
            }
        }
    }
}
