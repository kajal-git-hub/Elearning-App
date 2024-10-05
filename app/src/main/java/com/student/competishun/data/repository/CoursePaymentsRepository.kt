package com.student.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.coinkeeper.CoursePaymentsByUserIdQuery
import com.student.competishun.data.model.CoursePaymentDetail
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoursePaymentsRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {
    private val TAG = "CoursePaymentsRepository"

    suspend fun getCoursePaymentsByUserId(courseId: String, userId: String): List<CoursePaymentDetail>? {
        return try {
            val response = apolloClient.query(CoursePaymentsByUserIdQuery(courseId = courseId, userId = userId)).execute()
            Log.d(TAG, "Response: $response")

            if (response.hasErrors()) {
                response.errors?.forEach {
                    Log.e("$TAG Error", it.message)
                }
                return null
            }

            // Check if data is null
            response.data?.coursePaymentsByUserId?.let { payments ->
                Log.d(TAG, "Payments fetched: $payments")
                return payments.map {
                    CoursePaymentDetail(
                        paymentId = it.id ?: "",
                        courseId = it.entityId ?: "",
                        userId = it.userId ?: "",
                        paymentType = it.paymentType ?: "",
                        rzpOrderId = it.rzpOrderId ?: "",
                        amountPaid = it.amount ?: 0.0,
                        paymentStatus = it.status ?: ""
                    )
                }
            } ?: run {
                Log.d(TAG, "No course payments found.")
                return emptyList() // Or handle according to your requirements
            }
        } catch (e: ApolloException) {
            Log.e(TAG, "ApolloException: ${e.message}")
            e.printStackTrace()
            null
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
