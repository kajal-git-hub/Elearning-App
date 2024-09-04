package com.student.competishun.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.type.FindAllCourseInputStudent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.student.competishun.curator.GetAllBannersQuery
import com.student.competishun.curator.GetAllCourseLecturesCountQuery
import com.student.competishun.curator.type.FindAllBannersInput
import com.student.competishun.data.repository.StudentCourseRepository
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class StudentCoursesViewModel @Inject constructor(
    private val studentCourseRepository: StudentCourseRepository
) : ViewModel() {

    private val _courses = MutableStateFlow<Result<AllCourseForStudentQuery.Data>?>(null)
    val courses: StateFlow<Result<AllCourseForStudentQuery.Data>?> = _courses.asStateFlow()

    private val _lectures = MutableStateFlow<Result<GetAllCourseLecturesCountQuery.Data>?>(null)
    val lectures: StateFlow<Result<GetAllCourseLecturesCountQuery.Data>?> = _lectures.asStateFlow()

    private val _banners = MutableStateFlow<List<GetAllBannersQuery.Banner?>?>(null)
    val banners: StateFlow<List<GetAllBannersQuery.Banner?>?> = _banners


    fun fetchCourses(filters: FindAllCourseInputStudent) {
        viewModelScope.launch {
            val result = studentCourseRepository.getAllCourseForStudent(filters)
            _courses.value = result
            Log.e("StudentCoursesVM","${result}")
        }
    }

    fun fetchLectures(courseId: String) {
        viewModelScope.launch {
            val result = studentCourseRepository.getAllLectureCount(courseId)
            _lectures.value = result
            Log.e("LectureCountVM","${result}")
        }
    }


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchBanners(filters: FindAllBannersInput) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = studentCourseRepository.getAllBanners(filters)
                _banners.value = data
            } catch (e: Exception) {
                _banners.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}
