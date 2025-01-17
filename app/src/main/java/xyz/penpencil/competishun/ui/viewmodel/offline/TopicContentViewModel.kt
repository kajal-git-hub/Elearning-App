package xyz.penpencil.competishun.ui.viewmodel.offline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.data.room.repository.TopicContentModelRepository
import javax.inject.Inject

@HiltViewModel
class TopicContentViewModel @Inject constructor(
    private val repository: TopicContentModelRepository
) : ViewModel() {

    private val _topicContent = MutableStateFlow<List<TopicContentModel>>(emptyList())
    val topicContent: StateFlow<List<TopicContentModel>> = _topicContent.asStateFlow()

    private val _topicContentCountFileType = MutableStateFlow(CountFileType())
    val topicContentCountFileType: StateFlow<CountFileType> = _topicContentCountFileType.asStateFlow()

    init {
        getTopicContentCountByFileType()
    }

    fun insertTopicContent(topicContent: TopicContentModel) {
        viewModelScope.launch {
            repository.insertTopicContent(topicContent)
        }
    }

    fun deleteTopicContent(topicContent: TopicContentModel) {
        viewModelScope.launch {
            repository.deleteTopicContent(topicContent)
        }
    }

    fun clearTable() {
        viewModelScope.launch {
            repository.clearTable()
        }
    }

    fun getTopicContentByFileType(fileType: String): Flow<List<TopicContentModel>> {
        return repository.getTopicContentByFileType(fileType)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    fun getTopicContentCountByFileType() = viewModelScope.launch {
        combine(
            repository.getPdfCount(),
            repository.getVideoCount()
        ) { pdfCount, videoCount ->
            CountFileType(
                pdfCount = pdfCount,
                videoCount = videoCount,
                selectedCount = 0
            )
        }.collect { countFileType ->
            _topicContentCountFileType.value = countFileType
        }
    }

    fun updateSelectedCount(newCount: Int) {
        _topicContentCountFileType.value = _topicContentCountFileType.value.copy(selectedCount = newCount)
    }

}

data class CountFileType(
    var pdfCount: Int =0,
    var videoCount: Int =0,
    var selectedCount: Int = 0,
)
