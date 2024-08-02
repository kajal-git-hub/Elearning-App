package com.student.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.gatekeeper.VerifyOtpMutation
import com.student.competishun.data.api.Gatekeeper
import com.student.competishun.data.model.User
import com.student.competishun.data.model.UserInformation
import com.student.competishun.data.model.VerifyOtpResponse
import com.student.competishun.gatekeeper.type.VerifyOtpInput
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class VerifyOtpRepository @Inject constructor(@Gatekeeper private val apolloClient: ApolloClient) {

    suspend fun verifyOtp(verifyOtpInput: VerifyOtpInput): VerifyOtpResponse? {
        val mutation = VerifyOtpMutation(verifyOtpInput)

        return try {
            val response = apolloClient.mutate(mutation).execute()

            if (response.hasErrors()) {
                // Handle errors if needed
                response.errors?.forEach {
                    Log.e("GraphQL Error", it.message)
                }
                return null
            }

            response.data?.verifyOtp?.let { result ->
                VerifyOtpResponse(
                    user = result.user?.let { user ->
                        User(
                            mobileNumber = user.mobileNumber,
                            fullName = user.fullName,
                            countryCode = user.countryCode,
                            id = "",
                            userInformation = UserInformation(0,"","","","")
                        )
                    },
                    refreshToken = result.refreshToken,
                    accessToken = result.accessToken
                )
            }
        } catch (e: ApolloException) {
            // Handle ApolloException
            Log.e("ApolloException", e.message ?: "Unknown error")
            null
        }
    }
}
