package com.student.competishun.data.repository


import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.student.competishun.curator.AllCourseForStudentQuery
import javax.inject.Inject
import com.student.competishun.curator.type.FindAllCourseInputStudent
import com.student.competishun.data.api.Curator
import javax.inject.Singleton

@Singleton
class StudentCourseRepository @Inject constructor(
    @Curator private val apolloClient: ApolloClient
) {

    suspend fun getAllCourseForStudent(filters: FindAllCourseInputStudent): Result<AllCourseForStudentQuery.Data> {
        return try {
            val response = apolloClient.query(AllCourseForStudentQuery(filters))
                .execute()
            if (response.hasErrors()) {
                Result.failure(Exception(response.errors?.first()?.message))
            } else {
                Log.e("StudentCourseRepositodat","${response.data}")
                Result.success(response.data!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}