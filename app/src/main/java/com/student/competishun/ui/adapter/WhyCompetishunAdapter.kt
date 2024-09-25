package com.student.competishun.ui.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.WhyCompetishun

class WhyCompetishunAdapter(private val listWhyCompetishun: List<WhyCompetishun>): RecyclerView.Adapter<WhyCompetishunAdapter.WhyCompetishunViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhyCompetishunViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_why_competishun, parent, false)
        return WhyCompetishunViewHolder(view)
    }

    override fun onBindViewHolder(holder: WhyCompetishunViewHolder, position: Int) {
        val itemWhyCompetishun = listWhyCompetishun[position]
        holder.tvtitlename.text = itemWhyCompetishun.VideoTitle
        holder.tvtag1.text = itemWhyCompetishun.tag1
        holder.tvtag2.text = itemWhyCompetishun.tag2
        holder.itemplay.setOnClickListener{
            goToPlayerPage(holder.itemView.findNavController(), itemWhyCompetishun.videourl,"About this Course")
        }
    }

    private fun goToPlayerPage(navController: NavController, videourl: String,name:String) {
        val bundle = Bundle().apply {
            putString("url", videourl)
            putString("url_name", name)
        }
        Log.d("videourl",videourl)
        Log.d("Navigating to Explore","Navigating")
        navController.navigate(R.id.action_homeFragment_to_mediaFragment,bundle)
    }

    override fun getItemCount(): Int {
        return listWhyCompetishun.size
    }

    class WhyCompetishunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvtitlename: TextView =itemView.findViewById(R.id.VideoTitle)
        val tvtag1: TextView =itemView.findViewById(R.id.IIT_JEE)
        val tvtag2: TextView =itemView.findViewById(R.id.NEET_Cracked)
        val itemplay:ConstraintLayout = itemView.findViewById(R.id.item_why_comp)
    }

}