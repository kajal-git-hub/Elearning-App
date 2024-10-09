package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.databinding.SelectExamItemBinding // Generated binding class

class SelectExamAdapter(
    private val examList: List<String> // List of Exam objects
) : RecyclerView.Adapter<SelectExamAdapter.SelectExamViewHolder>() {

    // ViewHolder class using View Binding
    class SelectExamViewHolder(val binding: SelectExamItemBinding) : RecyclerView.ViewHolder(binding.root)

    private var selectedPosition: Int = -1 // To track the selected item

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectExamViewHolder {
        val binding = SelectExamItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectExamViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return examList.size
    }

    override fun onBindViewHolder(holder: SelectExamViewHolder, position: Int) {
        val exam = examList[position]
        val binding = holder.binding
        binding.radioButtonProfileExam.text = exam // Assuming you have a TextView with this ID

        // Update colors based on selection
        val isSelected = position == selectedPosition
        val textColorResId = if (isSelected) R.color.blue_3E3EF7 else R.color.recycler_txt
        binding.radioButtonProfileExam.setTextColor(ContextCompat.getColor(binding.root.context, textColorResId))

        // Update background based on selection
        binding.root.setBackgroundResource(if (isSelected) R.drawable.getstarted_itembg_selected else R.drawable.getstarted_itembg_unselected)

        val drawableResId = if (isSelected) R.drawable.property_done_check else R.drawable.property_default_check
        val drawable = ContextCompat.getDrawable(binding.root.context, drawableResId)
        binding.radioButtonProfileExam.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        // Handle item click and update selection
        holder.itemView.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousSelectedPosition) // Update the previous selected item
            notifyItemChanged(position) // Update the current selected item
        }
    }
}
