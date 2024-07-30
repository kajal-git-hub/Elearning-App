package com.student.competishun.utils

import com.student.competishun.curator.GetAllCourseCategoriesQuery

interface OnCourseItemClickListener {
    fun onCourseItemClick(course: GetAllCourseCategoriesQuery.GetAllCourseCategory)
}