package com.student.competishun.di

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.student.competishun.data.api.BASE_URL_COINKEEPER
import com.student.competishun.data.api.BASE_URL_CURATOR
import com.student.competishun.data.api.BASE_URL_GATEKEEPER
import com.student.competishun.data.api.Coinkeeper
import com.student.competishun.data.api.Curator
import com.student.competishun.data.api.Gatekeeper
import com.student.competishun.utils.CustomLoggingInterceptor
import com.student.competishun.utils.HelperFunctions
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
    fun provideApolloClient(sharedPreferencesManager: SharedPreferencesManager):ApolloClient{
        return ApolloClient.Builder()
            .serverUrl(BASE_URL_GATEKEEPER)
            .addHttpInterceptor(object : HttpInterceptor {
                override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {

                    val accessToken = sharedPreferencesManager.accessToken
                    val modifiedRequest = request.newBuilder()
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()
                    return chain.proceed(modifiedRequest)
                }
            })
            .build()
    }


    @Provides
    @Singleton
    @Curator
    fun provideApolloClientCurator(
        sharedPreferencesManager: SharedPreferencesManager
    ): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(BASE_URL_CURATOR)
            .addHttpInterceptor(object : HttpInterceptor {
                override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {

                    val accessToken = sharedPreferencesManager.accessToken
                    val modifiedRequest = request.newBuilder()
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()
                    return chain.proceed(modifiedRequest)
                }
            })
            .build()
    }

    @Provides
    @Singleton
    @Coinkeeper
    fun provideClientCoinkeeper(sharedPreferencesManager: SharedPreferencesManager):ApolloClient{
        return ApolloClient.Builder()
            .serverUrl(BASE_URL_COINKEEPER)
            .addHttpInterceptor(object : HttpInterceptor {
                override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {

                    val accessToken = sharedPreferencesManager.accessToken
                    val modifiedRequest = request.newBuilder()
                        .addHeader("Authorization", "Bearer $accessToken")
                        .build()
                    return chain.proceed(modifiedRequest)
                }
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideCustomLoggingInterceptor(): CustomLoggingInterceptor {
        return CustomLoggingInterceptor()
    }

    @Provides
    fun provideHelperFunctions(): HelperFunctions {
        return HelperFunctions()
    }
}
