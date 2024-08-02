package com.student.competishun.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.student.competishun.data.api.BASE_URL_CURATOR
import com.student.competishun.data.api.BASE_URL_GATEKEEPER
import com.student.competishun.data.api.Curator
import com.student.competishun.data.api.Gatekeeper
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
    @Gatekeeper
    fun provideApolloClient():ApolloClient{
        return ApolloClient.Builder()
            .serverUrl(BASE_URL_GATEKEEPER)
            .build()
    }


    @Provides
    @Singleton
    @Curator
    fun provideApolloClientCurator(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(BASE_URL_CURATOR)
            .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }

}
