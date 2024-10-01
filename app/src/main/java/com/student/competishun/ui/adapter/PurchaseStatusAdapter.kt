package com.student.competishun.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.student.competishun.R

class PurchaseStatusAdapter(
    private val context: Context,
    private val statusList: List<PurchaseStatus>,
    private val onStatusSelected: (String) -> Unit
) : RecyclerView.Adapter<PurchaseStatusAdapter.StatusViewHolder>() {

    private var selectedPosition: Int = 0

    class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusTextView: MaterialTextView = itemView.findViewById(R.id.et_orderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_filter_purchase, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val status = statusList[position]
        holder.statusTextView.text = status.statusText

        // Check if this item is selected
        val isSelected = position == selectedPosition
        holder.itemView.setBackgroundResource(
            if (isSelected) R.drawable.getstarted_itembg_selected else R.drawable.getstarted_itembg_unselected
        )

        // Set text color based on selection
        holder.statusTextView.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (isSelected) R.color.blue_3E3EF7 else R.color.black
            )
        )

        holder.itemView.setOnClickListener {
            // Update the selected position and notify the adapter
            val previousSelectedPosition = selectedPosition
            selectedPosition = position

            // Notify the previous and current selected positions to refresh their views
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(position)

            // Trigger the callback with the selected status
            onStatusSelected(status.statusText)
        }
    }

    override fun getItemCount(): Int = statusList.size
}

data class PurchaseStatus(val statusText: String)

