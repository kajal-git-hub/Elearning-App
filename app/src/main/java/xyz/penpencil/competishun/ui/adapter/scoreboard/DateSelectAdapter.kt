package xyz.penpencil.competishun.ui.adapter.scoreboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.scoreboard.DateItem

class DateSelectAdapter(private val items: List<DateItem>) :
    RecyclerView.Adapter<DateSelectAdapter.DateSelectViewHolder>() {

    class DateSelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBgSelect: ImageView = itemView.findViewById(R.id.iv_BgSelect)
        val textView: MaterialTextView = itemView.findViewById(R.id.et_textInside)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateSelectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_number_view, parent, false)
        return DateSelectViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateSelectViewHolder, position: Int) {
        val item = items[position]
        holder.ivBgSelect.setImageResource(item.imageResId)
        holder.textView.text = item.number.toString()
    }

    override fun getItemCount() = items.size
}
