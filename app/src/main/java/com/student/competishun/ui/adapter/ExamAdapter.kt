package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.databinding.GetstartedItemlayout2Binding

class ExampleAdapter(private val dataList: List<String>) :
    RecyclerView.Adapter<ExampleAdapter.ViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GetstartedItemlayout2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position], position == selectedPosition)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(private val binding: GetstartedItemlayout2Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, isSelected: Boolean) {
            binding.radioButton.text = item // Set text for the RadioButton
            binding.radioButton.isChecked = isSelected // Set checked state based on isSelected

            binding.radioButton.setOnClickListener {
                selectedPosition = adapterPosition // Update the selected position
                notifyDataSetChanged() // Notify the adapter to refresh the views
            }
        }
    }
}
