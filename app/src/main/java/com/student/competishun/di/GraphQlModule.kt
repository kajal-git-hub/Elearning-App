package com.student.competishun.di

import com.apollographql.apollo3.ApolloClient
import com.student.competishun.data.api.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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


}
