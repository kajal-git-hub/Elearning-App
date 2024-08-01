package com.student.competishun.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.InstallmentModel
import com.student.competishun.databinding.InstallementItemBinding

class InstallmentAdapter(
    private val installmentList: List<InstallmentModel>
) : RecyclerView.Adapter<InstallmentAdapter.InstallmentViewHolder>() {

    inner class InstallmentViewHolder(private val binding: InstallementItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(installment: InstallmentModel) {
            binding.tvInstallment.text = installment.title
            binding.tvAmount.text = installment.amount

            val nestedAdapter = NestedInstallmentAdapter(installment.nestedInstallmentList)
            binding.rvNestedInstallment.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = nestedAdapter
                isNestedScrollingEnabled = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstallmentViewHolder {
        val binding = InstallementItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return InstallmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InstallmentViewHolder, position: Int) {
        holder.bind(installmentList[position])
    }

    override fun getItemCount(): Int {
        return installmentList.size
    }
}
