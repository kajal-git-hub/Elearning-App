package xyz.penpencil.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.databinding.GetstartedItemlayoutBinding
import xyz.penpencil.competishun.databinding.InstallementItemBinding
import xyz.penpencil.competishun.databinding.SelectExamItemBinding
import xyz.penpencil.competishun.databinding.SelectSubjectItemBinding

class SubjectFilterAdapter(
    private val options: List<String>,
    private val selectedSubject: String,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SubjectFilterAdapter.SubjectFilterHolder>() {

    private var selectedPosition: Int = options.indexOf(selectedSubject).takeIf { it >= 0 } ?: RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectFilterHolder {
        val binding = SelectSubjectItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SubjectFilterHolder(binding)
    }
    // Method to clear the selection
    fun clearSelection() {
        val previousSelectedPosition = selectedPosition
        selectedPosition = -1
        previousSelectedPosition?.let { notifyItemChanged(it) }
    }

    override fun onBindViewHolder(holder: SubjectFilterHolder, position: Int) {
        val option = options[position]
        Log.e("selectedpostion",selectedPosition.toString())
        holder.bind(option, position == selectedPosition)

        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            // Notify to refresh the previous and current selected positions
            previousPosition?.let { notifyItemChanged(it) }
            notifyItemChanged(position)  // Highlight the new selection

            onItemClick(option)  // Pass selected item to the fragment or activity
        }
    }

    override fun getItemCount(): Int = options.size

    class SubjectFilterHolder(private val binding: SelectSubjectItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val textView: MaterialTextView = itemView.findViewById(R.id.radio_button_profileExam)

        fun bind(option: String, isSelected: Boolean) {
            binding.radioButtonProfileExam
            if (option == "12+" ){ textView.text = "12th +" }else
            textView.text = option

            if (isSelected) {
                // Selected state
                textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue_3E3EF7))
                val drawableResId = R.drawable.square_tick
                val drawable = ContextCompat.getDrawable(binding.root.context, drawableResId)
                val backgroundResId = R.drawable.bg_filter_selected
                binding.getStartedBgLayoutFirst.setBackgroundResource(backgroundResId)
                binding.radioButtonProfileExam.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            } else {

                textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.recycler_txt))
                val backgroundResId = R.drawable.bg_filter_purchase
                val drawableResId = R.drawable.property_default_check
                val drawable = ContextCompat.getDrawable(binding.root.context, drawableResId)
                binding.getStartedBgLayoutFirst.setBackgroundResource(backgroundResId)
                binding.radioButtonProfileExam.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }
        }
    }
}
