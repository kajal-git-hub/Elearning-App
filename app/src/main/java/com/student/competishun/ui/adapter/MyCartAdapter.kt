package com.student.competishun.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.student.competishun.data.model.CartItem
import com.student.competishun.databinding.MycartItemBinding

class MyCartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onItemClick: (CartItem) -> Unit
) : RecyclerView.Adapter<MyCartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: MycartItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {

            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = cartItems[position]
                    Log.d("MyCartAdapter", "Item clicked at position: $position with data: $item")
                    onItemClick(item)
                }
            }
        }
    }


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

    fun getItemAt(position: Int): CartItem? {
        return if (position in cartItems.indices) {
            cartItems[position]
        } else {
            null
        }
    }


    override fun getItemCount(): Int = cartItems.size
    fun updateCartItems(newCartItems: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newCartItems)
        notifyDataSetChanged()
    }
}
