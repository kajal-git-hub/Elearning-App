package com.student.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.http.HttpHeader
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.data.api.Gatekeeper
import com.student.competishun.gatekeeper.UpdateUserMutation
import com.student.competishun.data.model.UpdateUserResponse
import com.student.competishun.data.model.UserInformation
import com.student.competishun.gatekeeper.type.UpdateUserInput
import com.student.competishun.data.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateUserRepository @Inject constructor(@Gatekeeper private val apolloClient: ApolloClient) {

    private val TAG = "com.student.competishun.data.repository.UpdateUserRepository"

    suspend fun updateUser(updateUserInput: UpdateUserInput, accessToken: String): UpdateUserResponse? {
        val mutation = UpdateUserMutation(updateUserInput)
        val headers = listOf(HttpHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6ImM5ZTEzZmVmLTlhZjItNDUwYy1iYzM0LWFjNzdhNjYwZDY4ZSIsImlhdCI6MTcyMjkyNzEzMywiZXhwIjoxNzIyOTM3OTMzfQ.OTgVQNzabmmun4qbVobpMIHPNWCH0KUtVpDn8E9lshE"))

        return try {
            val response = apolloClient.mutate(mutation)
                .httpHeaders(headers)
                .execute()

            if (response.hasErrors()) {
                response.errors?.forEach {
                    Log.e("GraphQL Error", it.message)
                }
                return null
            }

            response.data?.updateUser?.let { user ->
                UpdateUserResponse(
                    user = User(
                        id = user.id,
                        mobileNumber = user.mobileNumber,
                        fullName = user.fullName,
                        countryCode = user.countryCode,
                        userInformation = UserInformation(
                            id = user.userInformation.id,
                            preparingFor = user.userInformation.preparingFor,
                            targetYear = user.userInformation.targetYear,
                            city = user.userInformation.city,
                            reference = user.userInformation.reference
                        )
                    )
                )
            } ?: run {
                Log.e(TAG, "User data is null in response")
                null
            }
        } catch (e: ApolloException) {
            Log.e(TAG, "ApolloException: ${e.message}")
            null
        }
    }
}
