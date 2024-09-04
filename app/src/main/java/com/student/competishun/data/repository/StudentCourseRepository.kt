package com.student.competishun.data.repository


import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.curator.AllCourseForStudentQuery
import com.student.competishun.curator.GetAllBannersQuery
import com.student.competishun.curator.GetAllCourseLecturesCountQuery
import com.student.competishun.curator.type.FindAllBannersInput
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
                Log.e("StudentCourseRepo","${response.data}")
                Result.success(response.data!!)
            }
        } catch (e: Exception) {
            Log.e("StudentCourseRepo", e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    suspend fun getAllLectureCount(courseId: String): Result<GetAllCourseLecturesCountQuery.Data> {
        return try {
            val response = apolloClient.query(GetAllCourseLecturesCountQuery(courseId))
                .execute()
            if (response.hasErrors()) {
                Result.failure(Exception(response.errors?.first()?.message))
            } else {
                Log.e("LectureRepository","${response.data}")
                Result.success(response.data!!)
            }
        } catch (e: Exception) {
            Log.e("LectureRepository", e.message ?: "Unknown error")
            Result.failure(e)

        }
    }

    suspend fun getAllBanners(filters: FindAllBannersInput?): List<GetAllBannersQuery.Banner?>? {
        val query = GetAllBannersQuery(Optional.presentIfNotNull(filters))

        return try {
            val response = apolloClient.query(query).execute()

            if (response.hasErrors()) {
                response.errors?.forEach {
                    Log.e("GraphQL Error", it.message)
                }
                return null
            }

            response.data?.getAllBanners?.banners
        } catch (e: ApolloException) {
            Log.e("BannersRepository", e.message ?: "Unknown error")
            null
        }
    }
}