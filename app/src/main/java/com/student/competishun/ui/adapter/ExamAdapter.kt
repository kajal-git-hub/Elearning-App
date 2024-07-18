package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.databinding.GetstartedItemlayout2Binding
import com.student.competishun.databinding.GetstartedItemlayoutBinding

class ExampleAdapter(
    private var dataList: List<String>,
    private var currentStep: Int,
    private var spanCount: Int

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedPosition = -1

    companion object {
        private const val VIEW_TYPE_STEP_0 = 0
        private const val VIEW_TYPE_STEP_1 = 1
        private const val VIEW_TYPE_STEP_OTHER = 2
    }
    override fun getItemViewType(position: Int): Int {
        return when (currentStep) {
            0 -> VIEW_TYPE_STEP_0
            1 -> VIEW_TYPE_STEP_1
            else -> VIEW_TYPE_STEP_OTHER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_STEP_0 -> {
                val binding = GetstartedItemlayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                Step0ViewHolder(binding)
            }
            VIEW_TYPE_STEP_1 -> {
                val binding = GetstartedItemlayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                Step1ViewHolder(binding)
            }

            else -> {
                val binding = GetstartedItemlayout2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                Step2ViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Step0ViewHolder -> holder.bind(dataList[position], position == selectedPosition)
            is Step1ViewHolder -> holder.bind(dataList[position], position == selectedPosition)
            is Step2ViewHolder -> holder.bind(dataList[position], position == selectedPosition)

        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateData(newData: List<String>,newStep: Int,newSpanCount: Int) {
        dataList = newData
        currentStep = newStep
        spanCount = newSpanCount
        notifyDataSetChanged()
    }

    inner class Step0ViewHolder(private val binding: GetstartedItemlayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, isSelected: Boolean) {
            binding.radioButton.text = item

            val textColorResId = if (isSelected) R.color.PrimaryColor else R.color.recycler_txt
            binding.radioButton.setTextColor(ContextCompat.getColor(binding.root.context, textColorResId))
            binding.root.setBackgroundResource(if(isSelected)R.drawable.getstarted_itembg_selected else R.drawable.getstarted_itembg_unselected)

            binding.radioButton.isChecked = isSelected
            binding.radioButton.setOnClickListener {
                selectedPosition = adapterPosition
                notifyDataSetChanged()
            }
        }
    }


    inner class Step1ViewHolder(private val binding: GetstartedItemlayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, isSelected: Boolean) {
            binding.radioButton.text = item

            val textColorResId = if (isSelected) R.color.PrimaryColor else R.color.recycler_txt
            binding.radioButton.setTextColor(ContextCompat.getColor(binding.root.context, textColorResId))
            binding.root.setBackgroundResource(if(isSelected)R.drawable.getstarted_itembg_selected else R.drawable.getstarted_itembg_unselected)

            binding.radioButton.isChecked = isSelected


            binding.radioButton.setOnClickListener {
                selectedPosition = adapterPosition
                notifyDataSetChanged()
            }
        }
    }
    inner class Step2ViewHolder(private val binding: GetstartedItemlayout2Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, isSelected: Boolean) {
            binding.radioButton.text = item

            val textColorResId = if (isSelected) R.color.PrimaryColor else R.color.recycler_txt
            binding.radioButton.setTextColor(ContextCompat.getColor(binding.root.context, textColorResId))
            binding.root.setBackgroundResource(if(isSelected)R.drawable.getstarted_itembg_selected else R.drawable.getstarted_itembg_unselected)
            binding.radioButton.isChecked = isSelected


            binding.radioButton.setOnClickListener {
                selectedPosition = adapterPosition
                notifyDataSetChanged()
            }
        }
    }

}
