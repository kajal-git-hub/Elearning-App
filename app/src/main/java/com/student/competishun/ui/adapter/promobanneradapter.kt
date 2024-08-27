package com.student.competishun.ui.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.student.competishun.R
import com.student.competishun.data.model.PromoBannerModel

class PromoBannerAdapter(private val promoBannerList: List<PromoBannerModel>) :
    RecyclerView.Adapter<PromoBannerAdapter.PromoBannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoBannerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.promo_banner_item, parent, false)
        return PromoBannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromoBannerViewHolder, position: Int) {
        val promoBanner = promoBannerList[position]
        holder.bind(promoBanner)
    }

    override fun getItemCount(): Int {
        return promoBannerList.size
    }

    class PromoBannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCourseName: ImageView = itemView.findViewById(R.id.ig_bannerImagehome)


        fun bind(promoBanner: PromoBannerModel) {
            Glide.with(itemView.context)
                .load(promoBanner.imageUrl)
                .into(tvCourseName)
        }
    }
}
