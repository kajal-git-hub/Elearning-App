package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.curator.MyCoursesQuery
import com.student.competishun.data.repository.MyCoursesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCoursesViewModel@Inject constructor(private val repository: MyCoursesRepository) : ViewModel() {

    private val _myCourses = MutableLiveData<Result<MyCoursesQuery.Data>>()
    val myCourses: LiveData<Result<MyCoursesQuery.Data>> get() = _myCourses

    fun fetchMyCourses() {
        viewModelScope.launch {
            val result = repository.getMyCourses()
            _myCourses.postValue(result)
        }
    }
}