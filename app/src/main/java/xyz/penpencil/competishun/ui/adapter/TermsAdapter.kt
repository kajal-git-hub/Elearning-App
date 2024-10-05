package xyz.penpencil.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.TermsItem

class TermsAdapter(private var termList: List<TermsItem>):
    RecyclerView.Adapter<TermsAdapter.TermViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.terms_item, parent, false)
        return TermViewHolder(view)
    }

    override fun getItemCount(): Int = termList.size

    override fun onBindViewHolder(holder: TermViewHolder, position: Int) {
        val termItem = termList[position]
        holder.bind(termItem)
    }
    inner class TermViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val additionalText: TextView = itemView.findViewById(R.id.additionalText)
        private val plusIcon: ImageView = itemView.findViewById(R.id.plusIcon)
        private val faq: ConstraintLayout = itemView.findViewById(R.id.cl_terms)

        fun bind(termItem: TermsItem) {
            questionText.text = termItem.question
            additionalText.text = termItem.details
            additionalText.visibility = if (termItem.isExpanded) View.VISIBLE else View.GONE
            plusIcon.setImageResource(if (termItem.isExpanded) R.drawable.minus else R.drawable.add)

            faq.setOnClickListener {
                termItem.isExpanded = !termItem.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }
    }
}