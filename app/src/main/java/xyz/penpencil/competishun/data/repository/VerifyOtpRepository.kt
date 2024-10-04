package xyz.penpencil.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.gatekeeper.VerifyOtpMutation
import xyz.penpencil.competishun.data.api.Gatekeeper
import xyz.penpencil.competishun.data.model.GoogleResponse
import xyz.penpencil.competishun.data.model.User
import xyz.penpencil.competishun.data.model.UserInformation
import xyz.penpencil.competishun.data.model.VerifyOtpResponse
import com.student.competishun.gatekeeper.GoogleAndroidAuthMutation
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
                            mobileNumber = user.mobileNumber?:"",
                            fullName = user.fullName?:"",
                            countryCode = user.countryCode?:"",
                            id = "",
                            email = "",
                            userInformation = UserInformation(0,"","","","","","","",null)
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

    suspend fun googleAuth(idToken: String): GoogleResponse? {
        val mutation = GoogleAndroidAuthMutation(idToken)

        return try {
            val response = apolloClient.mutate(mutation).execute()

            if (response.hasErrors()) {
                // Handle errors if needed
                response.errors?.forEach {
                    Log.e("GraphQL Error", it.message)
                }
                return null
            }

            response.data?.googleAndroidAuth?.let { result ->
                GoogleResponse(
                    user = result.user?.let { user ->
                        User(
                            mobileNumber = user.mobileNumber?:"",
                            fullName = user.fullName?:"",
                            countryCode = user.countryCode?:"",
                            id = "",
                            email = "",
                            userInformation = UserInformation(0,"","","","","","","",null)
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
