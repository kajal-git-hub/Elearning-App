package xyz.penpencil.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.RefundItem

class RefundAdapter(private var refundList: List<RefundItem>):RecyclerView.Adapter<RefundAdapter.RefundViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefundViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.refund_item, parent, false)
        return RefundViewHolder(view)
    }

    override fun getItemCount(): Int = refundList.size

    override fun onBindViewHolder(holder: RefundViewHolder, position: Int) {
        val policyItem = refundList[position]
        holder.bind(policyItem)
    }
    inner class RefundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionText: TextView = itemView.findViewById(R.id.questionText)
        private val additionalText: TextView = itemView.findViewById(R.id.additionalText)
        private val plusIcon: ImageView = itemView.findViewById(R.id.plusIcon)
        private val faq: ConstraintLayout = itemView.findViewById(R.id.cl_Refund)

        fun bind(refundItem: RefundItem) {
            questionText.text = refundItem.question
            additionalText.text = refundItem.details
            additionalText.visibility = if (refundItem.isExpanded) View.VISIBLE else View.GONE
            plusIcon.setImageResource(if (refundItem.isExpanded) R.drawable.minus else R.drawable.add)

            faq.setOnClickListener {
                refundItem.isExpanded = !refundItem.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }
    }
}