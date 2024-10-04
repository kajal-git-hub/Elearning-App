package xyz.penpencil.competishun.utils

import android.os.Bundle
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.GetAllCourseCategoriesQuery
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.data.model.TopicTypeModel

interface ToolbarCustomizationListener {
    fun onCustomizeToolbar(fileurl: String, fileType: String,ContentId:String)
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

interface OnDeleteClickListener {
    fun onDeleteClick(position: Int,item: TopicContentModel)
}