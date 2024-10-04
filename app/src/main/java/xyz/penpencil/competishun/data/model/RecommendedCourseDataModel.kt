package xyz.penpencil.competishun.data.model

import java.text.SimpleDateFormat
import java.util.*

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
) {
    fun getFormattedEndDate(): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date? = inputFormat.parse(endDate)

            val outputFormat = SimpleDateFormat("dd MMM, yy", Locale.getDefault())
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            endDate
        }
    }
}
