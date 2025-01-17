package xyz.penpencil.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.curator.GetCourseByIdQuery
import xyz.penpencil.competishun.data.repository.GetCourseByIDRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetCourseByIDViewModel @Inject constructor(
    private val courseByIDRepository: GetCourseByIDRepository
):ViewModel() {

    private val _courseByID = MutableLiveData<GetCourseByIdQuery.GetCourseById?>()
    val courseByID: LiveData<GetCourseByIdQuery.GetCourseById?> = _courseByID

    fun fetchCourseById(courseId: String) {

        viewModelScope.launch {
            _courseByID.value = courseByIDRepository.getCourseById(courseId)
        }
    }

}