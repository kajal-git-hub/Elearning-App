package com.student.competishun.data.model

class ItemModel(var name: String, var isSelected: Boolean)
data class TabItem(
    val discount: String,
    val courseName: String,
    val tags: List<String>,
    val startDate: String,
    val endDate: String,
    val lectures: String,
    val quizzes: String,
    val originalPrice: String,
    val discountPrice: String
)