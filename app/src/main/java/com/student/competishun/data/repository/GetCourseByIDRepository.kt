package com.student.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.curator.GetCourseByIdQuery
import com.student.competishun.data.api.Curator
import javax.inject.Inject

class GetCourseByIDRepository @Inject constructor(@Curator private val apolloClient: ApolloClient) {
    private val TAG = "GetCourseByIDRepository"
    suspend fun getCourseById(id: String): GetCourseByIdQuery.GetCourseById? {
        val response = try {
            apolloClient.query(GetCourseByIdQuery(id)).execute()
        } catch (e: ApolloException) {
            Log.e(TAG,e.message.toString())
            null
        }
        Log.e(TAG,response?.data.toString())
        return response?.data?.getCourseById
    }

}