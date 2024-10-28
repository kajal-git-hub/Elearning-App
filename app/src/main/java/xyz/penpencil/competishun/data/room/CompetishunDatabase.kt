package xyz.penpencil.competishun.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import xyz.penpencil.competishun.data.model.TopicContentModel
import xyz.penpencil.competishun.data.room.dao.TopicContentModelDao

@Database(
    entities = [TopicContentModel::class],
    version = 1,
    exportSchema = false
)
abstract class CompetishunDatabase : RoomDatabase() {
    abstract fun topicContentModelDao(): TopicContentModelDao
}