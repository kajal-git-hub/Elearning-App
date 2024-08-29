package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.curator.FindAllCourseFolderContentByScheduleTimeQuery
import com.student.competishun.curator.MyCoursesQuery
import com.student.competishun.data.repository.MyCoursesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCoursesViewModel@Inject constructor(private val repository: MyCoursesRepository) : ViewModel() {

    private val _myCourses = MutableLiveData<Result<MyCoursesQuery.Data>>()
    val myCourses: LiveData<Result<MyCoursesQuery.Data>> get() = _myCourses
    private val _courseFolderContent = MutableLiveData<Result<FindAllCourseFolderContentByScheduleTimeQuery.Data>>()
    val courseFolderContent: LiveData<Result<FindAllCourseFolderContentByScheduleTimeQuery.Data>> = _courseFolderContent


    fun fetchMyCourses() {
        viewModelScope.launch {
            val result = repository.getMyCourses()
            _myCourses.postValue(result)
        }
    }

  fun getCourseFolderContent(startDate: String, endDate: String, courseId: String) {
        viewModelScope.launch {
            val result = repository.findAllCourseFolderContentByScheduleTime(startDate, endDate, courseId)
            _courseFolderContent.postValue(result)
        }
    }


}
