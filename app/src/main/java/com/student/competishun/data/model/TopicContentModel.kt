package com.student.competishun.data.model

data class TopicContentModel(
    val subjectIcon: Int,
    val id:String,
    val playIcon: Int,
    val videoDuration : Int,
    val lecture: String,
    val lecturerName: String,
    val topicName: String,
    val topicDescription: String,
    val progress: Int,
    var url: String,
    val fileType: String,
    val lockTime:String
)
