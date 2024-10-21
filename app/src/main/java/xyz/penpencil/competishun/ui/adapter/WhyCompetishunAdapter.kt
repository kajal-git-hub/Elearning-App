package xyz.penpencil.competishun.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.WhyCompetishun
import xyz.penpencil.competishun.ui.main.YoutubeActivity

class WhyCompetishunAdapter(private val listWhyCompetishun: List<WhyCompetishun>): RecyclerView.Adapter<WhyCompetishunAdapter.WhyCompetishunViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhyCompetishunViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_why_competishun, parent, false)
        return WhyCompetishunViewHolder(view)
    }

    override fun onBindViewHolder(holder: WhyCompetishunViewHolder, position: Int) {
        val itemWhyCompetishun = listWhyCompetishun[position]
//        holder.tvtitlename.text = itemWhyCompetishun.VideoTitle
//        holder.tvtag1.text = itemWhyCompetishun.tag1
//        holder.tvtag2.text = itemWhyCompetishun.tag2
        Glide.with(holder.itemView)
            .load("https://img.youtube.com/vi/${extractYouTubeId(itemWhyCompetishun.videourl)}/0.jpg")
            .into(holder.thumbnail)
        holder.itemplay.setOnClickListener{
            goToPlayerPage(holder.itemView.context, itemWhyCompetishun.videourl,"About this Course")
        }
    }

    private fun goToPlayerPage(context: Context, videoUrl: String, name: String) {
        val intent = Intent(context, YoutubeActivity::class.java).apply {
            putExtra("url", videoUrl)
        }
        context.startActivity(intent)
    }



    fun extractYouTubeId(url: String): String? {
        val pattern = "(?:youtu\\.be/|youtube\\.com/(?:watch\\?v=|embed/|v/|.+\\?v=))([\\w-]{11})"
        val regex = Regex(pattern)
        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value
    }

    override fun getItemCount(): Int {
        return listWhyCompetishun.size
    }

    class WhyCompetishunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val tvtitlename: TextView =itemView.findViewById(R.id.VideoTitle)
//        val tvtag1: TextView =itemView.findViewById(R.id.IIT_JEE)
//        val tvtag2: TextView =itemView.findViewById(R.id.NEET_Cracked)
        val itemplay:ImageView = itemView.findViewById(R.id.play_buttonView)
        val thumbnail :ImageView = itemView.findViewById(R.id.bg_ytThumbnail)
    }

}