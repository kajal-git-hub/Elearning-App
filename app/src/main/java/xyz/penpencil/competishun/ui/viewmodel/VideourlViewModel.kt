package xyz.penpencil.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.penpencil.competishun.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.lifecycle.viewModelScope
import com.student.competishun.curator.type.UpdateVideoProgress
import kotlinx.coroutines.launch

@HiltViewModel
class VideourlViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _videoStreamUrl = MutableLiveData<String?>()
    val videoStreamUrl: LiveData<String?> get() = _videoStreamUrl


    fun fetchVideoStreamUrl(courseFolderContentId: String, format: String) {
        viewModelScope.launch {
            _videoStreamUrl.value = null
            val url = videoRepository.getVideoStreamUrl(courseFolderContentId, format)
            _videoStreamUrl.value = url

        }
    }

    private val _updateVideoProgressResult = MutableLiveData<Boolean>()
    val updateVideoProgressResult: LiveData<Boolean> get() = _updateVideoProgressResult

    fun updateVideoProgress(courseFolderContentId: String, currentDuration: Int) {
        val input = UpdateVideoProgress(courseFolderContentId, currentDuration)
        viewModelScope.launch {
            val success = videoRepository.updateVideoProgress(input)
            _updateVideoProgressResult.value = success
        }
    }
}
