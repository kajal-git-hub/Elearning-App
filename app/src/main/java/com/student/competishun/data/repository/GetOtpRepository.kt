package com.student.competishun.data.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.GetOtpMutation
import com.student.competishun.type.GetOtpInput
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetOtpRepository @Inject constructor(private val apolloClient: ApolloClient) {

    suspend fun getOtp(countryCode: String, mobileNumber: String): Boolean? {
        val getOtpInput = GetOtpInput(countryCode, mobileNumber)
        val mutation = GetOtpMutation(getOtpInput)
        return try {
            val response = apolloClient.mutation(mutation).execute()
            response.data?.getOtp
        } catch (e: ApolloException) {
            null
        }
    }
}
