package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.data.model.CoursePaymentDetail
import com.student.competishun.data.repository.CoursePaymentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoursePaymentsViewModel @Inject constructor(
    private val coursePaymentsRepository: CoursePaymentsRepository
) : ViewModel() {

    private val _coursePayments = MutableLiveData<List<CoursePaymentDetail>?>()
    val coursePayments: LiveData<List<CoursePaymentDetail>?> get() = _coursePayments

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchCoursePayments(courseId: String, userId: String) {
        viewModelScope.launch {
            try {
                val payments = coursePaymentsRepository.getCoursePaymentsByUserId(courseId, userId)
                _coursePayments.value = payments
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
