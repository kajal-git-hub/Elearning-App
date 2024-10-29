package xyz.penpencil.competishun.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import xyz.penpencil.competishun.data.room.CompetishunDatabase
import xyz.penpencil.competishun.data.room.dao.TopicContentModelDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CompetishunDatabase {
        return Room.databaseBuilder(context, CompetishunDatabase::class.java, "competishun_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTopicContentModelDao(database: CompetishunDatabase): TopicContentModelDao {
        return database.topicContentModelDao()
    }
}
