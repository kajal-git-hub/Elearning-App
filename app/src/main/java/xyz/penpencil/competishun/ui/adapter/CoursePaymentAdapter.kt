package xyz.penpencil.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import xyz.penpencil.competishun.R
import xyz.penpencil.competishun.data.model.CoursePaymentDetails

class CoursePaymentAdapter(
    private var coursePaymentList: List<CoursePaymentDetails>
) : RecyclerView.Adapter<CoursePaymentAdapter.CoursePaymentViewHolder>() {

    fun updateData(filteredList: List<CoursePaymentDetails>) {
        this.coursePaymentList = filteredList
        notifyDataSetChanged() // Refresh the RecyclerView
    }

    class CoursePaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPurchaseStatus: MaterialTextView = itemView.findViewById(R.id.tvPurchaseStatus)
        val tvCourseName: MaterialTextView = itemView.findViewById(R.id.tvCourseName)
        val ivStatusIcon: ImageView = itemView.findViewById(R.id.ivStatusIcon)
        val tvAmountPaid: MaterialTextView = itemView.findViewById(R.id.tvAmountPaidValue)
        val tvAmountPaidOn: MaterialTextView = itemView.findViewById(R.id.tvAmountPaidOnValue)
        val tvPaymentType: MaterialTextView = itemView.findViewById(R.id.tvPainmentTypeValue)
        val tvStudentRollNo: MaterialTextView = itemView.findViewById(R.id.tvStudentRollNoValue)
        val clNote: ConstraintLayout = itemView.findViewById(R.id.clNote)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoursePaymentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_purchase, parent, false)
        return CoursePaymentViewHolder(view)
    }

    // Bind the data to the views
    override fun onBindViewHolder(holder: CoursePaymentViewHolder, position: Int) {
        val currentItem = coursePaymentList[position]
        holder.tvPurchaseStatus.text = currentItem.purchaseStatus

        when(currentItem.purchaseStatus){
            "COMPLETE" -> holder.tvPurchaseStatus.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.tick_circle_schedule,0,0,0)
            "FAILED" -> holder.tvPurchaseStatus.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.failed_logo,0,0,0)
            "REFUND COMPLETE" -> holder.tvPurchaseStatus.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.refund_icon,0,0,0)
            "PAYMENT PROCESSING" -> holder.tvPurchaseStatus.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.process_payment_icon,0,0,0)
        }

        holder.tvCourseName.text = currentItem.courseName
        holder.ivStatusIcon.setImageResource(currentItem.statusIconRes)

        when(currentItem.purchaseStatus){
            "COMPLETE" -> holder.ivStatusIcon.setBackgroundResource(
                R.drawable.group_1707479053
            )
            "FAILED" -> holder.ivStatusIcon.setBackgroundResource(
                R.drawable.failed_status)
            "REFUND COMPLETE" -> holder.ivStatusIcon.setBackgroundResource(
                R.drawable.refund_status)
            "PAYMENT PROCESSING" -> holder.ivStatusIcon.setBackgroundResource(
                R.drawable.process_status)
        }


        holder.tvAmountPaid.text = currentItem.totalAmountPaid
        holder.tvAmountPaidOn.text = currentItem.amountPaidOn
        holder.tvPaymentType.text = currentItem.paymentType
        holder.tvStudentRollNo.text = currentItem.studentRollNo

        // Set visibility for refund note
        holder.clNote.visibility = if (currentItem.isRefundVisible) View.VISIBLE else View.GONE
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int = coursePaymentList.size
}
