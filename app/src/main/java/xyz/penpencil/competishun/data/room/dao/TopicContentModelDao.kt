package xyz.penpencil.competishun.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import xyz.penpencil.competishun.data.model.TopicContentModel

@Dao
interface TopicContentModelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: TopicContentModel)

    @Query("SELECT * FROM topic_content")
    fun getAllTopicContentModel(): Flow<List<TopicContentModel>>

    @Query("SELECT * FROM topic_content WHERE file_type = :fileType")
    fun getTopicContentByFileType(fileType: String): Flow<List<TopicContentModel>>

    @Query("SELECT COUNT(*) FROM topic_content WHERE file_type = :fileType")
    fun getTopicContentCountByFileType(fileType: String): Flow<Int>

    @Delete
    suspend fun delete(topicContentModel: TopicContentModel)

    @Query("SELECT COUNT(*) FROM topic_content WHERE file_type = 'PDF'")
    fun getPdfCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM topic_content WHERE file_type = 'VIDEO'")
    fun getVideoCount(): Flow<Int>
}