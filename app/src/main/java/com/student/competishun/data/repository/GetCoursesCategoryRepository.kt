package com.student.competishun.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.curator.GetAllCourseCategoriesQuery
import com.student.competishun.data.api.Curator
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GetCoursesCategoryRepository @Inject constructor(@Curator private val apolloClient: ApolloClient) {

    private val TAG = "CoursesCategoryRepository"

    @SuppressLint("LongLogTag")
    suspend fun getCourses(): List<GetAllCourseCategoriesQuery.GetAllCourseCategory>? {
        return try {
            val response = apolloClient.query(GetAllCourseCategoriesQuery()).execute()
            if (response.hasErrors()) {
                response.errors?.forEach {
                    Log.e("$TAG Error", it.message)
                }
                return null
            }
            response.data?.getAllCourseCategories
        } catch (e: ApolloException) {
            Log.e(TAG, e.message ?: "Unknown error")
            null
        }
    }
}