package xyz.penpencil.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.JeeAdvancedItem

class JEEAdvancedAdapter(private val jeeAdvancedItems: List<JeeAdvancedItem>) :
    RecyclerView.Adapter<JEEAdvancedAdapter.JEEAdvancedViewHolder>() {

    inner class JEEAdvancedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvtext500Right: MaterialTextView = itemView.findViewById(R.id.tvtext500Right)
        private val tvJEEMainsRight: MaterialTextView = itemView.findViewById(R.id.tvJEEMainsRight)
        private val tvBatchYearRight: MaterialTextView = itemView.findViewById(R.id.tvBatchYearRight)

        fun bind(jeeAdvancedItem: JeeAdvancedItem) {
            tvtext500Right.text = jeeAdvancedItem.text500
            tvJEEMainsRight.text = jeeAdvancedItem.jeeMainsText
            tvBatchYearRight.text = jeeAdvancedItem.batchYear
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JEEAdvancedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jee_advanced, parent, false)
        return JEEAdvancedViewHolder(view)
    }

    override fun onBindViewHolder(holder: JEEAdvancedViewHolder, position: Int) {
        holder.bind(jeeAdvancedItems[position])
    }

    override fun getItemCount(): Int {
        return jeeAdvancedItems.size
    }
}
