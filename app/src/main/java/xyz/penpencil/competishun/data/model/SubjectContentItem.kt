package xyz.penpencil.competishun.data.model

data class SubjectContentItem(
    val id:String,
    val chapterNumber: Int,
    val topicName: String,
    val topicDescription: String,
    val pdfcount: String,
    val videocount: String,
    val locktime:String,
    val progressPer:Int,
    val isExternal: Boolean = false
)