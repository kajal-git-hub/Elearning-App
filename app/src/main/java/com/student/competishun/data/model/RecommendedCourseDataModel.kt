package com.student.competishun.data.model

data class RecommendedCourseDataModel(
    val discount: String,
    val courseName: String,
    val tag1: String,
    val tag2: String,
    val tag3: String,
    val startDate: String,
    val endDate: String,
    val lectureCount: String,
    val quizCount: String,
    val originalPrice: String,
    val discountPrice: String
)
