package com.student.competishun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.CartItem
import com.student.competishun.databinding.MycartItemBinding

class MyCartAdapter(private val cartItems: List<CartItem>) :
    RecyclerView.Adapter<MyCartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: MycartItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = MycartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]

        with(holder.binding) {
            imgProfile.setImageResource(currentItem.profileImageResId)
            etCartNameText.text = currentItem.name
            etCartViewDetails.text = currentItem.viewDetails
            igForwardDetails.setImageResource(currentItem.forwardDetails)
        }
    }

    override fun getItemCount(): Int = cartItems.size
}
