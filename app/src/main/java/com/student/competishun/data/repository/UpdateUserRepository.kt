package com.student.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.gatekeeper.UpdateUserMutation
import com.student.competishun.data.model.UpdateUserResponse
import com.student.competishun.data.model.UserInformation
import com.student.competishun.gatekeeper.type.UpdateUserInput
import com.student.competishun.data.model.User
import com.apollographql.apollo3.api.http.HttpHeader
import com.student.competishun.data.api.Gatekeeper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateUserRepository @Inject constructor(@Gatekeeper private val apolloClient: ApolloClient) {

    private val TAG = "UpdateUserRepository"
    suspend fun updateUser(updateUserInput: UpdateUserInput, accessToken: String): UpdateUserResponse? {
        val mutation = UpdateUserMutation(updateUserInput)
        val headers = listOf(HttpHeader("Authorization", "Bearer $accessToken"))
        return try {
            val response = apolloClient.mutate(mutation)
                .httpHeaders(headers)
                .execute()

            if (response.hasErrors()) {
                // Handle errors if needed
                response.errors?.forEach {
                    Log.e("GraphQL Error", it.message)
                }
                return null
            }

            response.data?.updateUser?.let { result ->
                UpdateUserResponse(
                    user = result.let { user ->
                        User(
                            id = user.id,
                            mobileNumber = user.mobileNumber,
                            fullName = user.fullName,
                            countryCode = user.countryCode,
                            userInformation = user.userInformation.let { info ->
                                UserInformation(
                                    id = info.id,
                                    preparingFor = info.preparingFor,
                                    targetYear = info.targetYear,
                                    city = info.city,
                                    reference = info.reference
                                )
                            }
                        )
                    }
                )
            }
        } catch (e: ApolloException) {
            Log.e(TAG, e.message ?: "Unknown error")
            null
        }
    }
}
