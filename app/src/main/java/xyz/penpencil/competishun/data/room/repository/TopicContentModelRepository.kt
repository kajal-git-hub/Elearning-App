package xyz.penpencil.competishun.data.room.repository

import kotlinx.coroutines.flow.Flow
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.data.room.dao.TopicContentModelDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicContentModelRepository @Inject constructor(
    private val dao: TopicContentModelDao
) {
    val allTopicContent: Flow<List<TopicContentModel>> = dao.getAllTopicContentModel()

    fun getTopicContentByFileType(fileType: String): Flow<List<TopicContentModel>> {
        return dao.getTopicContentByFileType(fileType)
    }

    suspend fun insertTopicContent(topicContent: TopicContentModel) {
        dao.insert(topicContent)
    }

    suspend fun deleteTopicContent(topicContent: TopicContentModel) {
        dao.delete(topicContent)
    }
    suspend fun clearTable() {
        dao.clearTable()
    }

    fun getTopicContentCountByFileType(fileType: String): Flow<Int> {
        return dao.getTopicContentCountByFileType(fileType)
    }

    fun getPdfCount(): Flow<Int> = dao.getPdfCount()
    fun getVideoCount(): Flow<Int> = dao.getVideoCount()
}
