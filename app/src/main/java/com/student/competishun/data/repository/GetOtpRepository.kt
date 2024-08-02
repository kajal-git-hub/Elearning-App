package com.student.competishun.data.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.gatekeeper.GetOtpMutation
import com.student.competishun.data.api.Gatekeeper
import com.student.competishun.gatekeeper.type.GetOtpInput
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetOtpRepository @Inject constructor(  @Gatekeeper private val apolloClient: ApolloClient) {

    suspend fun getOtp(countryCode: String, mobileNumber: String): Boolean? {
        val getOtpInput = GetOtpInput(countryCode = countryCode, mobileNumber = mobileNumber)
        val mutation = GetOtpMutation(getOtpInput)
        return try {
            val response = apolloClient.mutation(mutation).execute()
            response.data?.getOtp
        } catch (e: ApolloException) {
            null
        }
    }
}
