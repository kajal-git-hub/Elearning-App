package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.student.competishun.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class VideourlViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _videoStreamUrl = MutableLiveData<String?>()
    val videoStreamUrl: LiveData<String?> get() = _videoStreamUrl

    fun fetchVideoStreamUrl(courseFolderContentId: String, format: String) {
        viewModelScope.launch {
            _videoStreamUrl.value = videoRepository.getVideoStreamUrl(courseFolderContentId, format)
        }
    }
}
