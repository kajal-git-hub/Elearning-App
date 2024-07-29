package com.student.competishun.data.model

// Define a sealed class to represent different item types
sealed class OurContentItem {
    data class FirstItem(val item: OurContentFirstItem) : OurContentItem()
    data class OtherItem(val item: OtherContentItem) : OurContentItem()
}



data class OurContentFirstItem(
    val iconResId: Int,
    val title: String,
    val isFree: Int
)

data class OtherContentItem(
    val iconResId2: Int,
    val title2: String,
    val lock: Int
)