package com.student.competishun.data.model

data class ScheduleData (
    val day: String,
    val date: String,
    val innerItems: List<InnerScheduleItem>
) {
    data class InnerScheduleItem (
        val subject_name:String,
        val topic_name:String,
        val lecture_start_time:String,
        val lecture_end_time:String,
        val file_url:String,
        val fileType:String,
        val contentId:String,
        val scheduleTimer:String
    )
}