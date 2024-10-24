package xyz.penpencil.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.curator.GetCourseByIdQuery
import xyz.penpencil.competishun.R

class OurContentAdapter(
    private var folderItems: List<GetCourseByIdQuery.Folder>,
    private val isItemSize: ObservableField<Boolean>,
    private var listener: OnItemClickListener

) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var isPurchased: Boolean = false

    interface OnItemClickListener {
        fun onFirstItemClick(folderId: String,folderName: String,free:Boolean,isPurchased:Boolean)
        fun onOtherItemClick(folderId: String,folderName: String,isPurchased:Boolean)
    }

    companion object {
        private const val VIEW_TYPE_FIRST_ITEM = 0
        private const val VIEW_TYPE_OTHER_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        val itemName = folderItems[position].name
        return when {
            itemName.startsWith("Class", ignoreCase = true) -> VIEW_TYPE_FIRST_ITEM
            else -> VIEW_TYPE_OTHER_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FIRST_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.our_content_firstitem, parent, false)
                FirstItemViewHolder(view)
            }
            VIEW_TYPE_OTHER_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.our_content_item, parent, false)
                OtherItemViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val folderItem = folderItems[position]
        when (holder) {
            is FirstItemViewHolder -> holder.bind(folderItem, listener,isPurchased)
            is OtherItemViewHolder -> holder.bind(folderItem, listener,isPurchased)
        }
    }

    override fun getItemCount(): Int {
        return if (isItemSize.get() == true) {
            minOf(folderItems.size, 3)

        } else {
            folderItems.size
        }
    }

    fun updateItems(newFolderItems: List<GetCourseByIdQuery.Folder>) {
        folderItems = newFolderItems
        notifyDataSetChanged()
    }

    fun updateContent(purchased: Boolean) {
      Log.e("purchasweData",purchased.toString())
        isPurchased = purchased
    }

    class FirstItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        private val freeBadgeImageView: ImageView = itemView.findViewById(R.id.freeBadgeImageView)
        fun bind(item: GetCourseByIdQuery.Folder, listener: OnItemClickListener,isPurchased: Boolean) {

            titleTextView.text = item.name
            if (isPurchased) {
                iconImageView.setImageResource(R.drawable.frame_1707480918)
                freeBadgeImageView.setImageResource(R.drawable.group_1272628769)
                val layoutParams = freeBadgeImageView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.marginEnd = 0
                freeBadgeImageView.layoutParams = layoutParams

            }
            else {
                iconImageView.setImageResource(R.drawable.frame_1707480918)
                freeBadgeImageView.setImageResource(R.drawable.lock)
                val paddingInPx = (4 * itemView.context.resources.displayMetrics.density).toInt()
                freeBadgeImageView.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
                val layoutParams = freeBadgeImageView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.marginEnd = (20 * itemView.context.resources.displayMetrics.density).toInt()
                freeBadgeImageView.layoutParams = layoutParams
            }
            Log.e("isspurchased",isPurchased.toString())

            itemView.setOnClickListener {
                listener.onFirstItemClick(item.id,item.name,true,isPurchased)
            }
        }
    }

    class OtherItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        private val lockImage: ImageView = itemView.findViewById(R.id.ig_lockImage)

        fun bind(item: GetCourseByIdQuery.Folder, listener: OnItemClickListener,isPurchased: Boolean) {
            titleTextView.text = item.name
            if (isPurchased) {
                iconImageView.setImageResource(R.drawable.frame_1707480918)
                lockImage.visibility = View.GONE
            }else{
                iconImageView.setImageResource(R.drawable.frame_1707480918)
                lockImage.setImageResource(R.drawable.lock)
            }
            itemView.setOnClickListener {
                listener.onOtherItemClick(item.id,item.name,isPurchased)
            }
        }
    }
}

