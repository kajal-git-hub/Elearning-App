package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.databinding.SelectYearItemBinding

class SelectYearAdapter(
    private val yearList: List<String>
) : RecyclerView.Adapter<SelectYearAdapter.SelectYearViewHolder>() {

    class SelectYearViewHolder(val binding: SelectYearItemBinding) : RecyclerView.ViewHolder(binding.root)

    private var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectYearViewHolder {
        val binding = SelectYearItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectYearViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return yearList.size
    }

    override fun onBindViewHolder(holder: SelectYearViewHolder, position: Int) {
        val year = yearList[position]
        val binding = holder.binding
        binding.radioButtonProfileYear.text = year

        val isSelected = position == selectedPosition
        val textColorResId = if (isSelected) R.color.blue_3E3EF7 else R.color.recycler_txt
        binding.radioButtonProfileYear.setTextColor(ContextCompat.getColor(binding.root.context, textColorResId))

        binding.root.setBackgroundResource(if (isSelected) R.drawable.getstarted_itembg_selected else R.drawable.getstarted_itembg_unselected)

        val drawableResId = if (isSelected) R.drawable.property_done_check else R.drawable.property_default_check
        val drawable = ContextCompat.getDrawable(binding.root.context, drawableResId)
        binding.radioButtonProfileYear.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        holder.itemView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(position)
        }
    }
}
