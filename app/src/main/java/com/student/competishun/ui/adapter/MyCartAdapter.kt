package com.student.competishun.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.student.competishun.R
import com.student.competishun.data.model.CartItem
import com.student.competishun.data.model.User
import com.student.competishun.databinding.MycartItemBinding
import com.student.competishun.ui.viewmodel.CreateCartViewModel
import com.student.competishun.utils.OnCartItemRemovedListener

class MyCartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val cartViewModel: CreateCartViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val  userId: String,
    private val onCartItemRemovedListener: OnCartItemRemovedListener,
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
            Glide.with(holder.itemView.context)
                .load(currentItem.profileImageResId)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(imgProfile)
            etCartNameText.text = currentItem.name
            etCartViewDetails.text = currentItem.viewDetails
            igDeleteIcon.setOnClickListener {
                removeCourse(holder.itemView.context,currentItem.cartId,position)
            }

        }
    }

    private fun removeCourse(context: Context, cartId: String, position: Int){
        cartViewModel.removeCart(cartId)
        cartViewModel.removeCartResult.observe(lifecycleOwner) { result ->
            result.onSuccess {
                cartItems.removeAt(position)
                // Notify the adapter about item removal
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, cartItems.size)
                onCartItemRemovedListener.onCartItemRemoved()

                cartViewModel.findAllCartItems(userId)
                Toast.makeText(context, "Cart removed successfully", Toast.LENGTH_SHORT).show()
            }.onFailure {
                // Handle failure, e.g., show an error message
              Log.e("Failed to remove cart: ",it.message.toString())
            }
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
