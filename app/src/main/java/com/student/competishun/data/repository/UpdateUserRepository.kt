package com.student.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.data.api.Gatekeeper
import com.student.competishun.data.model.Address
import com.student.competishun.gatekeeper.UpdateUserMutation
import com.student.competishun.data.model.UpdateUserResponse
import com.student.competishun.data.model.UserInformation
import com.student.competishun.gatekeeper.type.UpdateUserInput
import com.student.competishun.data.model.User
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UpdateUserRepository @Inject constructor(@Gatekeeper private val apolloClient: ApolloClient) {

    private val TAG = "UpdateUserRepository"

    suspend fun updateUser(
        updateUserInput: UpdateUserInput,
        documentPhoto: String?,
        passportPhoto: String?
    ): UpdateUserResponse? {
        val mutation = UpdateUserMutation(
            updateUserInput = updateUserInput,
            documentPhoto = Optional.present(documentPhoto)?:Optional.absent(),
            passportPhoto = Optional.present(passportPhoto)?:Optional.absent()
        )

        return try {
            val response = apolloClient.mutate(mutation).execute()

            if (response.hasErrors()) {
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
                            mobileNumber = user.mobileNumber?:"",
                            fullName = user.fullName,
                            countryCode = user.countryCode?:"",
                            email = user.email.toString(),
                            userInformation = user.userInformation?.let { info ->
                                UserInformation(
                                    id = user.id,
                                    preparingFor = info.preparingFor,
                                    targetYear = info.targetYear,
                                    reference = info.reference,
                                    tShirtSize = info.tShirtSize,
                                    documentPhoto = info.documentPhoto,
                                    schoolName = info.schoolName,
                                    waCountryCode = info.waCountryCode,
                                    address = info.address?.let { address-> Address(city = address.city) }

                                )
                            } ?: UserInformation(
                                id = user.id,
                                preparingFor = null,
                                targetYear = null,
                                reference = null,
                                tShirtSize = null,
                                documentPhoto = null,
                                schoolName = null,
                                waCountryCode = null,
                                address = null
                            )
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





