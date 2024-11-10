package xyz.penpencil.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.TopicTypeModel
import xyz.penpencil.competishun.databinding.ItemCourseTypeBinding

class TopicTypeAdapter(
    private val topicTypeList: List<TopicTypeModel>,
    private val preselectedTopic: String?, // Optional preselected topic type
    private val onItemClicked: (TopicTypeModel) -> Unit // Pass the entire model
) : RecyclerView.Adapter<TopicTypeAdapter.TopicTypeViewHolder>() {

    private var selectedPosition = topicTypeList.indexOfFirst { it.title == preselectedTopic }

    inner class TopicTypeViewHolder(private val binding: ItemCourseTypeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(topicTypeModel: TopicTypeModel, isSelected: Boolean) {
            // Set the text for the item
            binding.radioButtonCourseType.text = topicTypeModel.title

            // Update the background based on the selection status
            Log.e("isseleceted",isSelected.toString())
            binding.root.setBackgroundResource(
                if (isSelected) R.drawable.getstarted_itembg_selected else R.drawable.getstarted_itembg_unselected
            )

            // Update the drawable based on the selection status
            val drawableResId = if (isSelected) R.drawable.property_selected else R.drawable.property_default
            val drawable = ContextCompat.getDrawable(binding.root.context, drawableResId)
            binding.radioButtonCourseType.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

            // Handle click events
            binding.root.setOnClickListener {
                binding.root.setBackgroundResource(
                    R.drawable.getstarted_itembg_selected
                )
                val previousPosition = selectedPosition
                selectedPosition = bindingAdapterPosition

                // Refresh the previous and current selected items
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                // Pass the clicked model to the listener
                onItemClicked(topicTypeModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicTypeViewHolder {
        val binding = ItemCourseTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicTypeViewHolder, position: Int) {
        // Bind data to the ViewHolder, passing whether the item is selected
        Log.e("kajaldfdf $position",selectedPosition.toString())
        holder.bind(topicTypeList[position], position == selectedPosition)
    }

    override fun getItemCount(): Int {
        return topicTypeList.size
    }
}
