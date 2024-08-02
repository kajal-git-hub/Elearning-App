package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.curator.CoursesQuery
import com.student.competishun.data.repository.CoursesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val coursesRepository: CoursesRepository
) : ViewModel() {

    private val _courses = MutableLiveData<List<CoursesQuery.Course>?>()
    val courses: LiveData<List<CoursesQuery.Course>?> = _courses

    fun fetchCourses() {
        viewModelScope.launch {
            _courses.value = coursesRepository.getCourses()
        }
    }
}