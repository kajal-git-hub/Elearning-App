package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.databinding.GetstartedItemlayoutBinding

class ExampleAdapter(private val dataList: List<String>) :
    RecyclerView.Adapter<ExampleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            GetstartedItemlayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(private val binding: GetstartedItemlayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.radioButton.text = item // Set text for the RadioButton
        }
    }
}
