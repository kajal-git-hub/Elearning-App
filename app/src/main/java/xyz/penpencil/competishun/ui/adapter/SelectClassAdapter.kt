package xyz.penpencil.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.SelectClassItemBinding

class SelectClassAdapter(
    private val classList: List<String>
) : RecyclerView.Adapter<SelectClassAdapter.SelectClassViewHolder>() {

    // ViewHolder with binding
    class SelectClassViewHolder(val binding: SelectClassItemBinding) : RecyclerView.ViewHolder(binding.root)

    private var selectedPosition: Int = -1 // To track selected item

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectClassViewHolder {
        val binding = SelectClassItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectClassViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return classList.size
    }

    override fun onBindViewHolder(holder: SelectClassViewHolder, position: Int) {
        val className = classList[position]
        val binding = holder.binding
        binding.radioButtonProfile.text = className

        val isSelected = position == selectedPosition
        val textColorResId = if (isSelected) R.color.blue_3E3EF7 else R.color.recycler_txt
        binding.radioButtonProfile.setTextColor(ContextCompat.getColor(binding.root.context, textColorResId))

        binding.root.setBackgroundResource(if (isSelected) R.drawable.getstarted_itembg_selected else R.drawable.getstarted_itembg_unselected)

        val drawableResId = if (isSelected) R.drawable.property_done_check else R.drawable.property_default_check
        val drawable = ContextCompat.getDrawable(binding.root.context, drawableResId)
        binding.radioButtonProfile.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        holder.itemView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(position)
        }
    }
}
