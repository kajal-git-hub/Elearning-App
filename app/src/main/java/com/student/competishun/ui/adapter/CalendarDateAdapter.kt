package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.CalendarDate
import com.student.competishun.databinding.CalenderDateItemBinding

class CalendarDateAdapter(
    private val dates: List<CalendarDate>,
    private val onClick: (CalendarDate) -> Unit
) : RecyclerView.Adapter<CalendarDateAdapter.CalendarDateViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class CalendarDateViewHolder(private val binding: CalenderDateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(calendarDate: CalendarDate, isSelected: Boolean) {
            binding.tvDate.text = calendarDate.date
            binding.tvDay.text = calendarDate.day
            if (calendarDate.task != null) {
                binding.tvReminderCard.visibility = View.VISIBLE
                binding.tvReminderCard.text = calendarDate.task
            } else {
                binding.tvReminderCard.visibility = View.GONE
            }
            binding.viewIndicator.visibility = if (isSelected) View.VISIBLE else View.GONE
            binding.root.setOnClickListener {
                val previousSelectedPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)
                onClick(calendarDate)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDateViewHolder {
        val binding = CalenderDateItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalendarDateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarDateViewHolder, position: Int) {
        holder.bind(dates[position], position == selectedPosition)
    }

    override fun getItemCount() = dates.size

    fun setSelectedPosition(position: Int) {
        val previousSelectedPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousSelectedPosition)
        notifyItemChanged(selectedPosition)
    }
}

