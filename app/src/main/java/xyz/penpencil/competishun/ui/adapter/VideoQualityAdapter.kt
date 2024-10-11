package xyz.penpencil.competishun.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.VideoQualityItem

class VideoQualityAdapter(
    private val context: Context,
    private val itemList: List<VideoQualityItem>,
    private val onQualitySelected :(VideoQualityItem) -> Unit
) : RecyclerView.Adapter<VideoQualityAdapter.ViewHolder>() {

    private var selectedPosition = -1

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val qualityType: MaterialTextView = itemView.findViewById(R.id.radio_button_videoQuality_type)
        val fileSize: MaterialTextView = itemView.findViewById(R.id.tv_file_size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_video_quality_type, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.qualityType.text = item.qualityType
        holder.fileSize.text = item.fileSize

        val textColorResId = if (position == selectedPosition) {
            R.color.blue_3E3EF7
        } else {
            R.color.recycler_txt
        }
        holder.fileSize.setTextColor(ContextCompat.getColor(context, textColorResId))
        holder.qualityType.setTextColor(ContextCompat.getColor(context,textColorResId))

        val drawableResId = if (position == selectedPosition) {
            R.drawable.property_selected
        } else {
            R.drawable.property_default
        }
        val drawable = ContextCompat.getDrawable(context, drawableResId)
        holder.qualityType.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        holder.itemView.setBackgroundResource(
            if (position == selectedPosition) R.drawable.getstarted_itembg_selected
            else R.drawable.getstarted_itembg_unselected
        )

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            onQualitySelected(item)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
