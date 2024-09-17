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
    private var cartItems:List<CartItem>,
    private val cartViewModel: CreateCartViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val  userId: String,
    private val onCartItemRemovedListener: OnCartItemRemovedListener,
    private val onCartItemClickListener: OnCartItemClickListener,
    private val onItemClick: (CartItem) -> Unit
) : RecyclerView.Adapter<MyCartAdapter.CartViewHolder>() {
     var selectedItemPosition: Int = RecyclerView.NO_POSITION

    interface OnCartItemClickListener {
        fun onCartItemClicked(cartItem: CartItem)
    }

    inner class CartViewHolder(val binding: MycartItemBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentItem = cartItems[position]

                    if (currentItem.isFree) {
                        Toast.makeText(it.context, "Free items cannot be selected", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener // Don't proceed if it's a free item
                    }

                    cartItems.forEach { item ->
                        item.isSelected = false // Unselect all items
                    }
                    currentItem.isSelected = true // Select the clicked item // Refresh the list
                    // Notify the fragment about the selected item
                    onItemClick(currentItem)
                    notifyDataSetChanged()
                }
            }
        }
}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = MycartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }



    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[holder.bindingAdapterPosition]


        with(holder.binding) {
            Glide.with(holder.itemView.context)
                .load(currentItem.profileImageResId)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(imgProfile)
            if (currentItem.isFree) {
               igDeleteIcon.visibility = View.GONE
                ivFreeCartItem.visibility = View.VISIBLE

                //here its free course show free banners in it
            }else{
                igDeleteIcon.visibility = View.VISIBLE
                ivFreeCartItem.visibility = View.GONE
                //here its free course show free banners should not be visible in it
            }

            etCartNameText.text = currentItem.name
            etCartViewDetails.text = currentItem.viewDetails
            etCartViewDetails.setOnClickListener{
                onCartItemClickListener.onCartItemClicked(currentItem)
            }

            igDeleteIcon.setOnClickListener {
                igDeleteIcon.isEnabled = false
                if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION && holder.bindingAdapterPosition < cartItems.size) {
                    removeCourse(holder.itemView.context, currentItem.cartId, currentItem.cartItemId, holder.bindingAdapterPosition)
                }
               // if (position != RecyclerView.NO_POSITION && position < cartItems.size) {
                //    removeCourse(holder.itemView.context, currentItem.cartId, currentItem.cartItemId, position)
               // }
                igDeleteIcon.postDelayed({ igDeleteIcon.isEnabled = true }, 500)
            }

//            root.setOnClickListener {
//                cartItems.forEach{ item ->
//                    item.isSelected = false // Set the clicked item as selected
//                }
//                currentItem.isSelected = true
//
//                notifyDataSetChanged() // Refresh the list to highlight the selected item
//
//                // Notify the fragment about the selected item
//                onItemClick(currentItem)
//                Log.e("currentesma",currentItem.isSelected.toString())
//            }
            holder.binding.apply { root.isSelected = currentItem.isSelected}

            if (currentItem.isSelected) {
                root.setBackgroundResource(R.drawable.cartitem_bg) // Example of highlighting the selected item
            } else {
                root.setBackgroundResource(R.drawable.deffaultcartitem_bg) // Default background
            }


        }
    }

    fun getSelectedItem(): CartItem? {
        return if (selectedItemPosition != RecyclerView.NO_POSITION) {
            cartItems[selectedItemPosition]
        } else null
    }

    private fun removeCourse(context: Context, cartId: String,cartItemId:String, position: Int){
        cartViewModel.removeCartItem(cartItemId)
        cartViewModel.removeCartItemResult.observe(lifecycleOwner) { result ->

            result.onSuccess {
                val mutableCartItems = cartItems.toMutableList()
                if (position >= 0 && position < cartItems.size) {

                    mutableCartItems.removeAt(position)
                    cartItems = mutableCartItems
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartItems.size)
                    onCartItemRemovedListener.onCartItemRemoved()
                    cartViewModel.findAllCartItems(userId)
                    Toast.makeText(context, "Cart removed successfully", Toast.LENGTH_SHORT).show()
                }
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
        cartItems = newCartItems
        notifyDataSetChanged()
    }
}
