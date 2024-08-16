package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.TSizeModel
import com.student.competishun.databinding.ItemTSizeBinding

class TshirtSizeAdapter(
    private val tSizeList: List<TSizeModel>,
    private val onItemSelected: (String) -> Unit
) : RecyclerView.Adapter<TshirtSizeAdapter.TSizeViewHolder>() {

    private var selectedPosition = -1

    inner class TSizeViewHolder(private val binding: ItemTSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tSize: TSizeModel, isSelected: Boolean) {
            binding.SelectSize.text = tSize.t_size
            binding.root.setBackgroundResource(
                if (isSelected) R.drawable.tshirt_selected else R.drawable.getstarted_itembg_unselected
            )

            val drawableResId = if (isSelected) R.drawable.property_selected else R.drawable.property_default
            val drawable = ContextCompat.getDrawable(binding.root.context, drawableResId)
            binding.SelectSize.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                onItemSelected(tSize.t_size)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TSizeViewHolder {
        val binding = ItemTSizeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TSizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TSizeViewHolder, position: Int) {
        holder.bind(tSizeList[position], position == selectedPosition)
    }

    override fun getItemCount(): Int {
        return tSizeList.size
    }
}
