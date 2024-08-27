package com.student.competishun.data.repository

import com.apollographql.apollo3.ApolloClient
import com.student.competishun.curator.MyCoursesQuery
import com.student.competishun.data.api.Curator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyCoursesRepository@Inject constructor(@Curator private val apolloClient: ApolloClient) {

    suspend fun getMyCourses(): Result<MyCoursesQuery.Data> {
        return try {
            val response = apolloClient.query(MyCoursesQuery()).execute()

            if (response.hasErrors()) {
                Result.failure(Exception(response.errors?.firstOrNull()?.message))
            } else {
                val data = response.data
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("No data returned from the server"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
