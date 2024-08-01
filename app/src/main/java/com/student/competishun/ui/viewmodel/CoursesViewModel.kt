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
    private val _courses = MutableLiveData<List<GetAllCoursesQuery.GetAllCourse>?>()
    val courses: LiveData<List<GetAllCoursesQuery.GetAllCourse>?> = _courses
    init {
        this.helperFunctions = helperFunctions // Initialize here
    }

    fun fetchCourses(filters: FindAllCourseInput) {
        viewModelScope.launch {
            _courses.value = coursesRepository.getCourses(filters)
        }
    }

    fun getFormattedCourseStartDate(date: String?): String {
        return helperFunctions.formatCourseDate(date) ?: "No date available"
    }

    fun getDiscountDetails(originalPrice: Int, discountPrice: Int): Pair<Int, Int> {
        return helperFunctions.calculateDiscountDetails(originalPrice, discountPrice)
    }



}

