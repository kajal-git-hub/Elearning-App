package xyz.penpencil.competishun.data.model

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

data class Course(
    val category_name: String?,
    val discount: String?,
    val name: String?,
    val tags: List<String>?,
    val course_start_date: String?,
    val course_validity_end_date: String?,
    val lectures: String?,
    val quizzes: String?,
    val originalPrice: String?,
    val discountPrice: String?
)