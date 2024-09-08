package com.student.competishun.utils

import android.os.Bundle
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.GetAllCourseCategoriesQuery
import com.student.competishun.curator.MyCoursesQuery
import com.student.competishun.data.model.TopicTypeModel

interface OnLectureCountRequestedListener {
    fun onLectureCountRequested(courseId: String, position: Int)
}

interface OnCourseItemClickListener {
    fun onCourseItemClick(course: GetAllCourseCategoriesQuery.GetAllCourseCategory)
}

interface StudentCourseItemClickListener {
    fun onCourseItemClicked(course: AllCourseForStudentQuery.Course,bundle: Bundle)
}

interface OnCartItemRemovedListener {
    fun onCartItemRemoved()
}
interface OnTopicTypeSelectedListener {
    fun onTopicTypeSelected(selectedTopic: TopicTypeModel)
}