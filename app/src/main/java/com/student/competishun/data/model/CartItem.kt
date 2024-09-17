package com.student.competishun.data.model

import com.student.competishun.curator.type.CourseLectureCount

data class CartItem(
    val profileImageResId: String,
    val name: String,
    val viewDetails: String,
    val forwardDetails: Int,
    val discount: Int,
    val price: Int,
    val cartItemId:String,
    val entityId: String,
    val cartId: String,
    val courseId: String,
    val withInstallmentPrice: Int,
    val categoryId: String,
    val isFree: Boolean = false,
    var isSelected: Boolean = false,
    var recommendCourseTags: List<String>?

)
