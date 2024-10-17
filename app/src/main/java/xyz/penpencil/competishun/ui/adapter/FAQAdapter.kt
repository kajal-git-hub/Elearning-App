package xyz.penpencil.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.FAQItem

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
    fun updateAdapter(limitedFaqItems: List<FAQItem>) {
        faqList =  limitedFaqItems
        notifyDataSetChanged()
    }

    inner class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val additionalText: TextView = itemView.findViewById(R.id.additionalText)
        private val plusIcon: ImageView = itemView.findViewById(R.id.plusIcon)
        private val faq: ConstraintLayout = itemView.findViewById(R.id.cl_faq)

        fun bind(faqItem: FAQItem) {
            questionText.text = faqItem.question
            additionalText.text = faqItem.details
            additionalText.visibility = if (faqItem.isExpanded) View.VISIBLE else View.GONE
            plusIcon.setImageResource(if (faqItem.isExpanded) R.drawable.minus else R.drawable.add)

            faq.setOnClickListener {
                faqItem.isExpanded = !faqItem.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }
    }
}
