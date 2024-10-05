package xyz.penpencil.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.penpencil.competishun.data.model.InstallmentItemModel
import xyz.penpencil.competishun.databinding.RvnestedItemBinding

class NestedInstallmentAdapter(
    private val nestedInstallmentList: List<InstallmentItemModel>
) : RecyclerView.Adapter<NestedInstallmentAdapter.NestedInstallmentViewHolder>() {

    inner class NestedInstallmentViewHolder(private val binding: RvnestedItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InstallmentItemModel) {
            binding.tvDescription.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedInstallmentViewHolder {
        val binding = RvnestedItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NestedInstallmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NestedInstallmentViewHolder, position: Int) {
        holder.bind(nestedInstallmentList[position])
    }

    override fun getItemCount(): Int {
        return nestedInstallmentList.size
    }
}
