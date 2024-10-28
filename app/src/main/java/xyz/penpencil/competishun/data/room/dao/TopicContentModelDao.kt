package xyz.penpencil.competishun.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xyz.penpencil.competishun.data.model.TopicContentModel

@Dao
interface TopicContentModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: TopicContentModel)

    @Query("SELECT * FROM topic_content")
    suspend fun getAllTopicContentModel(): List<TopicContentModel>

    @Delete
    suspend fun delete(topicContentModel: TopicContentModel)
}