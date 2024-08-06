package com.student.competishun.ui.adapter

import com.student.competishun.data.model.TSizeModel


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.databinding.ItemTSizeBinding

class TshirtSizeAdapter(private val tSizeList: List<TSizeModel>) : RecyclerView.Adapter<TshirtSizeAdapter.TSizeViewHolder>() {

    inner class TSizeViewHolder(private val binding: ItemTSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tSize: TSizeModel) {
            binding.radioButtonTSize.text = tSize.t_size
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TSizeViewHolder {
        val binding = ItemTSizeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TSizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TSizeViewHolder, position: Int) {
        holder.bind(tSizeList[position])
    }


    override fun getItemCount(): Int {
        return tSizeList.size
    }


}
