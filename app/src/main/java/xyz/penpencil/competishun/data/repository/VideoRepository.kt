package xyz.penpencil.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.curator.GetVideoStreamDataSignedUrlQuery
import com.student.competishun.curator.UpdateVideoProgressMutation
import com.student.competishun.curator.type.UpdateVideoProgress
import xyz.penpencil.competishun.data.api.Curator
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

    suspend fun updateVideoProgress(updateVideoProgress: UpdateVideoProgress): Boolean {
        val mutation = UpdateVideoProgressMutation(updateVideoProgress)

        return try {
            val response = apolloClient.mutation(mutation).execute()

            if (response.hasErrors()) {
                // Handle errors if needed
                response.errors?.forEach {
                    Log.e("GraphQL Error", it.message)
                }
                false
            } else {
                Log.e("VideoRepository", response.data?.updateVideoProgress?.current_duration.toString())
                response.data?.updateVideoProgress != null

            }
        } catch (e: ApolloException) {
            // Handle ApolloException
            Log.e("VideoRepository", e.message ?: "Unknown error")
            false
        }
    }
}
