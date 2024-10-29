package xyz.penpencil.competishun.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.io.Serializable

@Entity(tableName = "topic_content")
data class TopicContentModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "subject_icon")
    val subjectIcon: Int,

    @ColumnInfo(name = "play_icon")
    val playIcon: Int,

    @ColumnInfo(name = "video_duration")
    val videoDuration: Int,

    @ColumnInfo(name = "lecture")
    val lecture: String,

    @ColumnInfo(name = "lecturer_name")
    val lecturerName: String,

    @ColumnInfo(name = "topic_name")
    val topicName: String,

    @ColumnInfo(name = "topic_description")
    val topicDescription: String,

    @ColumnInfo(name = "progress")
    val progress: Int,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "file_type")
    val fileType: String,

    @ColumnInfo(name = "lock_time")
    val lockTime: String,

    @ColumnInfo(name = "homework_url")
    val homeworkUrl: String,

    @ColumnInfo(name = "homework_name")
    val homeworkName: String,

    @ColumnInfo(name = "is_external")
    var isExternal: Boolean = false,

    @ColumnInfo(name = "local_path")
    var localPath: String = ""
) : Serializable
