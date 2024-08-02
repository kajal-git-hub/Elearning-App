package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.curator.GetAllCourseCategoriesQuery
import com.student.competishun.data.repository.GetCoursesCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CoursesCategoryViewModel @Inject constructor(
    private val coursesCategoryRepository: GetCoursesCategoryRepository
) : ViewModel() {

    private val _coursesCategory = MutableLiveData<List<GetAllCourseCategoriesQuery.GetAllCourseCategory>?>()
    val coursesCategory: LiveData<List<GetAllCourseCategoriesQuery.GetAllCourseCategory>?> = _coursesCategory


    fun fetchCoursesCategory() {
        viewModelScope.launch {
            _coursesCategory.value = coursesCategoryRepository.getCourses()
        }
    }
}

