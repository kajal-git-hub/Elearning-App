package xyz.penpencil.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.JeeItem

class JEEAdapter(private val jeeItems: List<JeeItem>) :
    RecyclerView.Adapter<JEEAdapter.JEEViewHolder>() {

    inner class JEEViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvtext500: MaterialTextView = itemView.findViewById(R.id.tvtext500)
        private val tvJEEMains: MaterialTextView = itemView.findViewById(R.id.tvJEEMains)
        private val tvBatchYear: MaterialTextView = itemView.findViewById(R.id.tvBatchYear)

        fun bind(jeeItem: JeeItem) {
            tvtext500.text = jeeItem.text500
            tvJEEMains.text = jeeItem.jeeMainsText
            tvBatchYear.text = jeeItem.batchYear
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JEEViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jee_main, parent, false)
        return JEEViewHolder(view)
    }

    override fun onBindViewHolder(holder: JEEViewHolder, position: Int) {
        holder.bind(jeeItems[position])
    }

    override fun getItemCount(): Int {
        return jeeItems.size
    }
}
