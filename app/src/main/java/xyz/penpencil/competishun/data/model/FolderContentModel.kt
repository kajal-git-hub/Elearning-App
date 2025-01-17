package xyz.penpencil.competishun.data.model

import java.io.Serializable

data class FolderContentModel(
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
    val lockTime:String,
    val homeworkUrl : String,
    val homeworkName : String
): Serializable
