package com.student.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.curator.GetAllCourseQuery
import com.student.competishun.curator.type.FindAllCourseInput
import com.student.competishun.data.api.Curator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoursesRepository @Inject constructor(@Curator private val apolloClient: ApolloClient) {

    private val TAG = "CoursesRepository"

    suspend fun getCourses(filters: FindAllCourseInput?): GetAllCourseQuery.GetAllCourse? {
        return try {
            val response = apolloClient.query(GetAllCourseQuery(
                filters = Optional.presentIfNotNull(filters)
            )).execute()
            if (response.hasErrors()) {
                response.errors?.forEach {
                    Log.e("$TAG Error", it.message)
                }
                return null
            }
            response.data?.getAllCourse
        } catch (e: ApolloException)
        {
            Log.e(TAG, e.message ?: "Unknown error")
            null
        }
    }
}
