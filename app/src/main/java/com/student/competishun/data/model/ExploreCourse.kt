package com.student.competishun.data.model

import android.os.Parcelable
import com.student.competishun.curator.MyCoursesQuery

data class ExploreCourse(
    val name: String,
    val className: String,
    val courseType: String,
    val target: String,
    val ongoingStatus: String,
    val percentCompleted: Int,
    val hasFreeFolder: Boolean = false,
    val folderIds: List<MyCoursesQuery.Folder>?
)

