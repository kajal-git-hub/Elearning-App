package xyz.penpencil.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.TSizeModel
import xyz.penpencil.competishun.databinding.ItemTSizeBinding

class TshirtSizeAdapter(
    private val tSizeList: List<TSizeModel>,
    private val preselectedSize: String?, // Optional preselected size
    private val onItemSelected: (String) -> Unit
) : RecyclerView.Adapter<TshirtSizeAdapter.TSizeViewHolder>() {

    private var selectedPosition = tSizeList.indexOfFirst { it.t_size == preselectedSize }

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
