package xyz.penpencil.competishun.data.model

data class CoursePaymentDetails(
    val purchaseStatus: String,
    val courseName: String,
    val statusIconRes: Int,
    val totalAmountPaid: String,
    val amountPaidOn: String,
    val paymentType: String,
    val studentRollNo: String,
    val isRefundVisible: Boolean,
    val enrolledCourseId: String,
    val userId:String
)
