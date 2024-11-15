package xyz.penpencil.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.GetstartedItemlayoutBinding
import xyz.penpencil.competishun.databinding.InstallementItemBinding
import xyz.penpencil.competishun.databinding.SelectExamItemBinding


class ExamFilterAdapter(
    private val options: List<String>,
    private var autoselectedExam: String,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ExamFilterAdapter.ExamFilterHolder>() {
    private var selectedPosition: Int = options.indexOf(autoselectedExam).takeIf { it >= 0 } ?: RecyclerView.NO_POSITION


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamFilterHolder {
        val binding = SelectExamItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ExamFilterHolder(binding)
    }

    override fun onBindViewHolder(holder: ExamFilterHolder, position: Int) {
        val option = options[position]
        holder.bind(option, position == selectedPosition)

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            previousPosition?.let { notifyItemChanged(it) }
            notifyItemChanged(position)  // Highlight the new selection

            onItemClick(option)  // Pass selected item to the fragment
        }
    }
    // Method to clear the selection
    fun clearSelection() {
        val previousSelectedPosition = selectedPosition
        selectedPosition = -1
        previousSelectedPosition.let { notifyItemChanged(it) }
    }

    override fun getItemCount(): Int = options.size

    class ExamFilterHolder(private val binding: SelectExamItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val textView: MaterialTextView = itemView.findViewById(R.id.radio_button_profileExam)

        fun bind(option: String, isSelected: Boolean) {
            textView.text = option

            if (isSelected) {
                textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue_3E3EF7))
                val drawableResId = if (isSelected) R.drawable.square_tick else R.drawable.property_default_check
                val drawable = ContextCompat.getDrawable(binding.root.context, drawableResId)
                val backgroundResId = if (isSelected) R.drawable.bg_filter_selected else R.drawable.getstarted_itembg_unselected
                binding.getStartedBgLayoutFirst.setBackgroundResource(backgroundResId)
               // itemView.setBackgroundResource(drawableResId)
                binding.radioButtonProfileExam.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null)
            } else {
                textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.recycler_txt))
                itemView.setBackgroundResource(R.drawable.bg_filter_purchase)
            }
        }
    }
}
