package xyz.penpencil.competishun.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.curator.FindCourseFolderProgressQuery
import com.student.competishun.curator.FindCourseParentFolderProgressQuery
import com.student.competishun.curator.GetAllCourseQuery
import com.student.competishun.curator.type.FindAllCourseInput
import xyz.penpencil.competishun.data.repository.CoursesRepository
import xyz.penpencil.competishun.utils.HelperFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.di.Result
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


    private val _courseFolderProgress = MutableLiveData<Result<FindCourseFolderProgressQuery.Data>>()
    val courseFolderProgress: LiveData<Result<FindCourseFolderProgressQuery.Data>> = _courseFolderProgress

    fun findCourseFolderProgress(findCourseFolderProgressId: String) {
        _courseFolderProgress.value = Result.Loading // Set to Loading state

        viewModelScope.launch {
            val result = coursesRepository.findCourseFolderProgress(findCourseFolderProgressId)
            _courseFolderProgress.value = result
        }
    }

    private val _courseParentFolderProgress = MutableLiveData<Result<FindCourseParentFolderProgressQuery.Data>>()
    val courseParentFolderProgress: LiveData<Result<FindCourseParentFolderProgressQuery.Data>> = _courseParentFolderProgress

    fun findCourseParentFolderProgress(courseId: String) {
        _courseParentFolderProgress.value = Result.Loading // Set to Loading state

        viewModelScope.launch {
            val result = coursesRepository.findCourseParentFolderProgress(courseId)
            _courseParentFolderProgress.value = result
        }
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
   //to get date formated
    fun getFormattedCourseStartDate(date: String?): String {
        return helperFunctions.formatCourseDate(date)
    }
    //to get discount
    fun getDiscountDetails(originalPrice: Double, discountPrice: Double): Pair<Int, Int> {
        return helperFunctions.calculateDiscountDetails(originalPrice, discountPrice)
    }



}

