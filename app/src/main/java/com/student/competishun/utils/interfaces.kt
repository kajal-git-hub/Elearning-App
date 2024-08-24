package com.student.competishun.utils

import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.GetAllCourseCategoriesQuery

interface OnCourseItemClickListener {
    fun onCourseItemClick(course: GetAllCourseCategoriesQuery.GetAllCourseCategory)
}

interface StudentCourseItemClickListener {
    fun onCourseItemClicked(course: AllCourseForStudentQuery.Course)
}

interface OnCartItemRemovedListener {
    fun onCartItemRemoved()
}