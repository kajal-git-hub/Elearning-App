package xyz.penpencil.competishun.di

import android.content.Context
import android.os.Build
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.student.competishun.gatekeeper.GetNewTokensMutation
import xyz.penpencil.competishun.data.api.BASE_URL_COINKEEPER
import xyz.penpencil.competishun.data.api.BASE_URL_CURATOR
import xyz.penpencil.competishun.data.api.BASE_URL_GATEKEEPER
import xyz.penpencil.competishun.data.api.Coinkeeper
import xyz.penpencil.competishun.data.api.Curator
import xyz.penpencil.competishun.data.api.Gatekeeper
import xyz.penpencil.competishun.utils.CustomLoggingInterceptor
import xyz.penpencil.competishun.utils.HelperFunctions
import xyz.penpencil.competishun.utils.SharedPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import xyz.penpencil.competishun.BuildConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GraphQlModule {

    @Provides
    @Singleton
    @Gatekeeper
    fun provideApolloClient(sharedPreferencesManager: SharedPreferencesManager): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(BuildConfig.BASE_URL_GATEKEEPER)
            .addHttpInterceptor(createHttpInterceptor(sharedPreferencesManager))
            .build()
    }

    @Provides
    @Singleton
    @Curator
    fun provideApolloClientCurator(sharedPreferencesManager: SharedPreferencesManager): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(BuildConfig.BASE_URL_CURATOR)
            .addHttpInterceptor(createHttpInterceptor(sharedPreferencesManager))
            .build()
    }

    @Provides
    @Singleton
    @Coinkeeper
    fun provideClientCoinkeeper(sharedPreferencesManager: SharedPreferencesManager): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(BuildConfig.BASE_URL_COINKEEPER)
            .addHttpInterceptor(createHttpInterceptor(sharedPreferencesManager))
            .build()
    }

    private fun createHttpInterceptor(sharedPreferencesManager: SharedPreferencesManager): HttpInterceptor {
        return object : HttpInterceptor {
            override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
                val accessToken = sharedPreferencesManager.accessToken
                var modifiedRequest = request.newBuilder()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()

                var response = chain.proceed(modifiedRequest)
//{"errors":[{"message":"Unauthorized","statusCode":"UNAUTHENTICATED"}],"data":null}
//                if (response.body?.readUtf8()?.contains("{\"message\":\"Unauthorized\",\"statusCode\":\"UNAUTHENTICATED\"}") == true) {
                if (response.statusCode == 401) {
                    val newAccessToken = refreshToken(sharedPreferencesManager)
                    if (newAccessToken != null) {
                        modifiedRequest = request.newBuilder()
                            .addHeader("Authorization", "Bearer $newAccessToken")
                            .build()
                        response = chain.proceed(modifiedRequest)
                    }
                }

                return response
            }
        }
    }

    private suspend fun refreshToken(
        sharedPreferencesManager: SharedPreferencesManager): String? {
        val newAccessToken = getNewAccessTokenFromRefreshToken(sharedPreferencesManager)
        return if (newAccessToken != null) {
            sharedPreferencesManager.accessToken = newAccessToken
            newAccessToken
        } else {
            null
        }
    }

    private suspend fun getNewAccessTokenFromRefreshToken(sharedPreferencesManager: SharedPreferencesManager): String? {
        val apolloClient = ApolloClient.Builder()
            .serverUrl(BASE_URL_GATEKEEPER)
            .addHttpHeader("Authorization", "Bearer ${sharedPreferencesManager.refreshToken}")
            .build()

        return try {
            val mutation = GetNewTokensMutation()
            val response = apolloClient.mutation(mutation).execute()

            if (response.hasErrors()) {
                response.errors?.forEach { error ->
                    println("Apollo Error: ${error.message}")
                }
                null
            } else {
                val authData = response.data?.getNewTokens
                if (authData != null) {
                    sharedPreferencesManager.refreshToken = authData.refreshToken
                    sharedPreferencesManager.accessToken = authData.accessToken
                    authData.accessToken
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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
