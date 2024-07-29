package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.FAQItem

class FAQAdapter(private var faqList: List<FAQItem>) :
    RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.faq_item, parent, false)
        return FAQViewHolder(view)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faqItem = faqList[position]
        holder.bind(faqItem)
    }

    override fun getItemCount(): Int = faqList.size

    inner class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val additionalText: TextView = itemView.findViewById(R.id.additionalText)
        private val plusIcon: ImageView = itemView.findViewById(R.id.plusIcon)

        fun bind(faqItem: FAQItem) {
            questionText.text = faqItem.question
            additionalText.visibility = if (faqItem.isExpanded) View.VISIBLE else View.GONE
            plusIcon.setImageResource(if (faqItem.isExpanded) R.drawable.minus else R.drawable.add)

            plusIcon.setOnClickListener {
                faqItem.isExpanded = !faqItem.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }
    }
}
