package com.student.competishun.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.student.competishun.R

class PurchaseStatusAdapter(
    private val context: Context,
    private val statusList: List<PurchaseStatus>,
    private val onStatusSelected: (String) -> Unit
) : RecyclerView.Adapter<PurchaseStatusAdapter.StatusViewHolder>() {

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
        holder.itemView.setOnClickListener {
            onStatusSelected(status.statusText)
        }
    }

    override fun getItemCount(): Int = statusList.size
}
data class PurchaseStatus(val statusText: String)

