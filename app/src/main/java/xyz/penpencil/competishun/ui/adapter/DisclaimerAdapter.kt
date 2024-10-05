package xyz.penpencil.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.DisclaimerItem

class DisclaimerAdapter(private var disclaimerList: List<DisclaimerItem>):RecyclerView.Adapter<DisclaimerAdapter.DisclaimerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisclaimerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.disclaimer_item, parent, false)
        return DisclaimerViewHolder(view)
    }

    override fun getItemCount(): Int = disclaimerList.size

    override fun onBindViewHolder(holder: DisclaimerViewHolder, position: Int) {
        val policyItem = disclaimerList[position]
        holder.bind(policyItem)
    }
    inner class DisclaimerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val additionalText: TextView = itemView.findViewById(R.id.additionalText)
        private val plusIcon: ImageView = itemView.findViewById(R.id.plusIcon)
        private val faq: ConstraintLayout = itemView.findViewById(R.id.cl_Disclaimer)

        fun bind(disclaimerItem: DisclaimerItem) {
            questionText.text = disclaimerItem.question
            additionalText.text = disclaimerItem.details
            additionalText.visibility = if (disclaimerItem.isExpanded) View.VISIBLE else View.GONE
            plusIcon.setImageResource(if (disclaimerItem.isExpanded) R.drawable.minus else R.drawable.add)

            faq.setOnClickListener {
                disclaimerItem.isExpanded = !disclaimerItem.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }
    }
}