package xyz.penpencil.competishun.data.model

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
