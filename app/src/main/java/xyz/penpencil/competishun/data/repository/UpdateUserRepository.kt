package xyz.penpencil.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import xyz.penpencil.competishun.data.api.Gatekeeper
import xyz.penpencil.competishun.data.model.Address
import com.student.competishun.gatekeeper.UpdateUserMutation
import xyz.penpencil.competishun.data.model.UpdateUserResponse
import xyz.penpencil.competishun.data.model.UserInformation
import com.student.competishun.gatekeeper.type.UpdateUserInput
import xyz.penpencil.competishun.data.api.ApiProcess
import xyz.penpencil.competishun.data.model.User
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


    suspend fun updateUserErrorHandled(
        updateUserInput: UpdateUserInput,
        documentPhoto: String?,
        passportPhoto: String?
    ): ApiProcess<UpdateUserResponse> {
        val mutation = UpdateUserMutation(
            updateUserInput = updateUserInput,
            documentPhoto = documentPhoto?.let { Optional.Present(it) } ?: Optional.Absent,
            passportPhoto = passportPhoto?.let { Optional.Present(it) } ?: Optional.Absent
        )

        return try {
            val response = apolloClient.mutation(mutation).execute()

            if (response.hasErrors()) {
                // Collect the error messages into one message string
                val message = response.errors?.joinToString(separator = ", ") { it.message } ?: "Unknown error"
                response.errors?.forEach { Log.e("GraphQL Error", it.message) }

                // Return failure with error message
                ApiProcess.Failure(message = message)
            } else {
                response.data?.updateUser?.let { result ->
                    // Constructing the `User` and `UserInformation` based on the response data
                    val user = User(
                        id = result.id,
                        mobileNumber = result.mobileNumber ?: "",
                        fullName = result.fullName,
                        countryCode = result.countryCode ?: "",
                        email = result.email.toString(),
                        userInformation = result.userInformation?.let { info ->
                            UserInformation(
                                id = result.id,
                                preparingFor = info.preparingFor,
                                targetYear = info.targetYear,
                                reference = info.reference,
                                tShirtSize = info.tShirtSize,
                                documentPhoto = info.documentPhoto,
                                schoolName = info.schoolName,
                                waCountryCode = info.waCountryCode,
                                address = info.address?.let { address -> Address(city = address.city) }
                            )
                        } ?: UserInformation(
                            id = result.id,
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

                    // Return success with the user response
                    ApiProcess.Success(UpdateUserResponse(user = user))
                } ?: ApiProcess.Failure(message = "Failed to parse user data")
            }
        } catch (e: ApolloException) {
            Log.e(TAG, e.message ?: "Unknown error")
            ApiProcess.Failure(message = "Unknown error: ${e.message ?: "No message"}")
        }
    }

}





