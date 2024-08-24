package com.student.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.curator.GetVideoStreamDataSignedUrlQuery
import com.student.competishun.data.api.Curator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(@Curator private val apolloClient: ApolloClient) {

    suspend fun getVideoStreamUrl(courseFolderContentId: String, format: String): String? {
        val query = GetVideoStreamDataSignedUrlQuery(courseFolderContentId, format)

        return try {
            val response = apolloClient.query(query).execute()

            if (response.hasErrors()) {
                // Handle errors if needed
                response.errors?.forEach {
                    Log.e("GraphQL Error", it.message)
                }
                return null
            }

            response.data?.getVideoStreamDataSignedUrl?.signedUrl
        } catch (e: ApolloException) {
            // Handle ApolloException
            Log.e("VideoRepository", e.message ?: "Unknown error")
            null
        }
    }
}
