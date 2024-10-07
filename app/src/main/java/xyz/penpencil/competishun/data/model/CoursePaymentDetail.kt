package xyz.penpencil.competishun.data.model

data class CoursePaymentDetail(
    val paymentId: String,
    val courseId: String,
    val userId: String,
    val paymentType: String,
    val rzpOrderId: String,
    val amountPaid: Double,
    val paymentStatus: String
)
