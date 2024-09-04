package com.student.competishun.data.model

import com.student.competishun.curator.FindCourseFolderProgressQuery

data class FreeDemoItem(
    val id:String,
    val playIcon:Int,
    var titleDemo:String,
    var timeDemo:String,
    var fileUrl: String,
    var fileType: String,
    var subFolderduration: List<FindCourseFolderProgressQuery.Folder1>?
)
