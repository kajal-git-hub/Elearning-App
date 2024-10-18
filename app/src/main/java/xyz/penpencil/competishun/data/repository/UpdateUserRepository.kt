package xyz.penpencil.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.gatekeeper.UpdateUserMutation
import com.student.competishun.gatekeeper.type.UpdateUserInput
import xyz.penpencil.competishun.data.api.ApiProcess
import xyz.penpencil.competishun.data.api.Gatekeeper
import xyz.penpencil.competishun.data.model.Address
import xyz.penpencil.competishun.data.model.UpdateUserResponse
import xyz.penpencil.competishun.data.model.User
import xyz.penpencil.competishun.data.model.UserInformation
import javax.inject.Inject
import javax.inject.Singleton
import okio.source
import com.apollographql.apollo3.api.Upload
import xyz.penpencil.competishun.utils.FileUpload
import java.io.File


@Singleton
class UpdateUserRepository @Inject constructor(@Gatekeeper private val apolloClient: ApolloClient) {

    private val TAG = "UpdateUserRepository"

    suspend fun updateUser(
        updateUserInput: UpdateUserInput,
        documentPhoto: String?,
        passportPhoto: String?
    ): UpdateUserResponse? {
//        val mutation = UpdateUserMutation(
//            updateUserInput = updateUserInput,
//            passportPhoto = if (passportPhoto != null) {
//                Optional.presentIfNotNull(FileUpload(File(passportPhoto), "image/*"))
//            } else {
//                Optional.absent()
//            },
//            documentPhoto = if (documentPhoto != null) {
//                Optional.presentIfNotNull(FileUpload(File(documentPhoto), "image/*"))
//            } else {
//                Optional.absent()
//            }
//        )

        val mutation = UpdateUserMutation(
            updateUserInput = updateUserInput,
            passportPhoto = if (passportPhoto != null) {
                Optional.presentIfNotNull(passportPhoto)
            } else {
                Optional.absent()
            },
            documentPhoto = if (documentPhoto != null) {
                Optional.presentIfNotNull(documentPhoto)
            } else {
                Optional.absent()
            }
        )

        return try {
            val response = apolloClient.mutation(mutation).execute()

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
                                    dob = info.dob,
                                    gender = info.gender,
                                    addressLine1 = info.address?.addressLine1,
                                    address = Address(city = info.address?.city, state = info.address?.state)

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
                                address = null,
                                dob = null,
                                gender = null,
                                addressLine1 = "",
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
                                dob = info.dob,
                                gender = info.gender,
                                addressLine1 = info.address?.addressLine1,
                                address = Address(city = info.address?.city, state = info.address?.state)


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
                            address = null,
                            dob = null,
                            gender = null,
                            addressLine1 = ""
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





