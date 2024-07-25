package com.student.competishun.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R

class PlayerlistAdapter() : RecyclerView.Adapter<PlayerlistAdapter.PlayerlistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerlistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_testimonials_card, parent, false)
        return PlayerlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerlistViewHolder, position: Int) {

        holder.userProfileIcon.setOnClickListener {

            val url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            goToPlayerPage(holder.itemView.findNavController(), url)
        }

    }

    private fun goToPlayerPage(navController: NavController, url: String) {
        val bundle = Bundle().apply {
            putString("url", url)
        }
        navController.navigate(R.id.action_homeFragment_to_mediaFragment,bundle)
    }

    override fun getItemCount(): Int {
        return testimonials.size
    }

    class PlayerlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userProfileIcon: ImageView = itemView.findViewById(R.id.userProfileIcon)

    }
}
