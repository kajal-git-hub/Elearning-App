package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.R
import com.student.competishun.data.model.PolicyItem

class PolicyAdapter(private var policyList: List<PolicyItem>):
    RecyclerView.Adapter<PolicyAdapter.PolicyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PolicyAdapter.PolicyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.policy_item, parent, false)
        return PolicyViewHolder(view)
    }

    override fun getItemCount(): Int = policyList.size

    override fun onBindViewHolder(holder: PolicyAdapter.PolicyViewHolder, position: Int) {
        val policyItem = policyList[position]
        holder.bind(policyItem)
    }
    inner class PolicyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val additionalText: TextView = itemView.findViewById(R.id.additionalText)
        private val plusIcon: ImageView = itemView.findViewById(R.id.plusIcon)
        private val faq: ConstraintLayout = itemView.findViewById(R.id.cl_Policy)

        fun bind(termItem: PolicyItem) {
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