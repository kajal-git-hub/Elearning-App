package xyz.penpencil.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import xyz.penpencil.competishun.R

class StudyCoursesAdapter(
    private val options: List<String>,
    private val autoSelectedExam: String,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<StudyCoursesAdapter.ToggleViewHolder>() {
    private var selectedPosition: Int = options.indexOf(autoSelectedExam).takeIf { it >= 0 } ?: RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToggleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_filter_purchase, parent, false)
        return ToggleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToggleViewHolder, position: Int) {
        val option = options[position]
        holder.bind(option, position == selectedPosition)

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)  // Unhighlight the previous selection
            notifyItemChanged(position)  // Highlight the new selection

            onItemClick(option)  // Pass selected item to the fragment
        }
    }

    override fun getItemCount(): Int = options.size

    class ToggleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: MaterialTextView = itemView.findViewById(R.id.et_orderStatus)

        fun bind(option: String, isSelected: Boolean) {
            textView.text = option
            // Highlight the selected item by changing background color or text style
            if (isSelected) {
                textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue_3E3EF7))
                itemView.setBackgroundResource(R.drawable.bg_filter_selected)
            } else {
                textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.recycler_txt))
                itemView.setBackgroundResource(R.drawable.bg_filter_purchase)
            }
        }
    }
}
