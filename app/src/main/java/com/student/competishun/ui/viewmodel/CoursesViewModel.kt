package com.student.competishun.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.curator.GetAllCourseQuery
import com.student.competishun.curator.type.FindAllCourseInput
import com.student.competishun.data.repository.CoursesRepository
import com.student.competishun.utils.HelperFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val coursesRepository: CoursesRepository, private var helperFunctions: HelperFunctions
) : ViewModel() {

    // Initialize here
    private val _courses = MutableLiveData<List<GetAllCourseQuery.Course>?>()
    val courses: LiveData<List<GetAllCourseQuery.Course>?> = _courses
    init {
        this.helperFunctions = helperFunctions
    }
    private val _courseCount = MutableLiveData<Double?>()
    val courseCount: LiveData<Double?> = _courseCount

    fun fetchCourses(filters: FindAllCourseInput) {
        viewModelScope.launch {
            val response = coursesRepository.getCourses(filters)
            _courses.value = response?.courses
            _courseCount.value = response?.count
        }
    }

    fun getFormattedCourseStartDate(date: String?): String {
        return helperFunctions.formatCourseDate(date) ?: "No date available"
    }

    fun getDiscountDetails(originalPrice: Int, discountPrice: Int): Pair<Int, Int> {
        return helperFunctions.calculateDiscountDetails(originalPrice, discountPrice)
    }



}

