package xyz.penpencil.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.data.model.CalendarDate
import xyz.penpencil.competishun.databinding.CalenderDateItemBinding

class CalendarDateAdapter(
    val dates: List<CalendarDate>,
    private val onClick: (CalendarDate) -> Unit,
    private var hasScheduleList: MutableList<String>
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

            if (hasScheduleList.contains(calendarDate.date)) {
                Log.e("calendaradapter",hasScheduleList.toString() +"+++"+ calendarDate.date)
                binding.dotContentAvailable.visibility = View.VISIBLE
            } else {
                binding.dotContentAvailable.visibility = View.GONE
            }

            binding.viewIndicator.visibility = if (isSelected) View.VISIBLE else View.GONE
            binding.root.setOnClickListener {
                if (binding.dotContentAvailable.visibility != View.VISIBLE) {
                    Toast.makeText(it.context, "No classes have been scheduled on this date", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val previousSelectedPosition = selectedPosition
                selectedPosition = absoluteAdapterPosition
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

    fun updateHasScheduleList(newHasScheduleList: MutableList<String>) {
        hasScheduleList = newHasScheduleList
        notifyDataSetChanged()
    }
}


