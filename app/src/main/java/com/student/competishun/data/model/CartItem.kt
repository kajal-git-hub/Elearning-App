package com.student.competishun.data.model

data class CartItem(
    val profileImageResId: String, // Assuming a drawable resource ID
    val name: String,
    val viewDetails: String,
    val forwardDetails: Int, // Assuming a drawable resource ID for the arrow icon
    val discount: Int,
    val price: Int,
    val cartItemId:String,
    val entityId: String,
    val cartId: String,
    val courseId: String,
    val withInstallmentPrice: Int,
    val categoryId: String,
    val isFree: Boolean = false

)
