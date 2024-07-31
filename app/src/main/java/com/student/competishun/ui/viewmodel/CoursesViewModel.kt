package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.curator.GetAllCourseCategoriesQuery
import com.student.competishun.curator.GetAllCoursesQuery
import com.student.competishun.curator.type.FindAllCourseInput
import com.student.competishun.data.repository.CoursesRepository
import com.student.competishun.data.repository.GetCoursesCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val coursesRepository: CoursesRepository,
    private val coursesCategoryRepository: GetCoursesCategoryRepository
) : ViewModel() {

    private val _courses = MutableLiveData<List<GetAllCoursesQuery.GetAllCourse>?>()
    val courses: LiveData<List<GetAllCoursesQuery.GetAllCourse>?> = _courses
    private val _coursesCategory = MutableLiveData<List<GetAllCourseCategoriesQuery.GetAllCourseCategory>?>()
    val coursesCategory: LiveData<List<GetAllCourseCategoriesQuery.GetAllCourseCategory>?> = _coursesCategory


    fun fetchCourses(filters: FindAllCourseInput) {
        viewModelScope.launch {
            _courses.value = coursesRepository.getCourses(filters)
        }
    }

    fun fetchCoursesCategory() {
        viewModelScope.launch {
            _coursesCategory.value = coursesCategoryRepository.getCourses()
        }
    }
}

