package xyz.penpencil.competishun.ui.adapter


import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.PromoBannerModel

class PromoBannerAdapter(
    private val promoBannerList: List<PromoBannerModel>,
    private val onItemClick: (String?,String?) -> Unit
) :
    RecyclerView.Adapter<PromoBannerAdapter.PromoBannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoBannerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.promo_banner_item, parent, false)
        return PromoBannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromoBannerViewHolder, position: Int) {
        val promoBanner = promoBannerList[position]
        holder.bind(promoBanner)

        holder.itemView.setOnClickListener {
            onItemClick(promoBanner.redirectLink , promoBanner.courseId)  // Invoke the lambda with the redirect link
        }
    }

    override fun getItemCount(): Int {
        return promoBannerList.size
    }

    class PromoBannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivBannerImage: ImageView = itemView.findViewById(R.id.ig_bannerImagehome)

        fun bind(promoBanner: PromoBannerModel) {
            Glide.with(itemView.context)
                .asBitmap()
                .load(promoBanner.imageUrl)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        ivBannerImage.setImageBitmap(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        ivBannerImage.setImageResource(R.drawable.promo_banner_home)
                    }
                })
        }

    }
}
