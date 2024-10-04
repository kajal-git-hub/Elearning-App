package xyz.penpencil.competishun.data.model

data class NotificationItem(
    val startUrl:Int,
    val title: String,
    val description: String,
    val imageUrl: String?,
)

data class NotificationSection(
    val sectionTitle: String, // e.g., "Today", "Yesterday"
    val notifications: List<NotificationItem>
)

