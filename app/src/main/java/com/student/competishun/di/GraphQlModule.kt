package com.student.competishun.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.student.competishun.data.api.BASE_URL
import com.student.competishun.utils.SharedPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GraphQlModule {

    @Provides
    @Singleton
    fun provideBaseUrl() = BASE_URL

    @Provides
    @Singleton
    fun provideApolloClient(baseUrl:String):ApolloClient{
        return ApolloClient.Builder()
            .serverUrl(baseUrl)
            .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }

}
