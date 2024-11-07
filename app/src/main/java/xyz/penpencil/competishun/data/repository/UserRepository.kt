package xyz.penpencil.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.http.HttpHeader
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.gatekeeper.DeleteMyAccountMutation
import xyz.penpencil.competishun.data.api.Gatekeeper
import com.student.competishun.gatekeeper.MyDetailsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class UserRepository  @Inject constructor(@Gatekeeper private val apolloClient: ApolloClient) {

    private val TAG = "UserRepository"

    suspend fun getMyDetails(): Result<MyDetailsQuery.Data> {
        return try {
            val response = apolloClient.query(MyDetailsQuery()).execute()
            if (response.hasErrors()) {
                Result.failure(Exception(response.errors?.first()?.message))
            } else {
                Log.e(TAG,response.data.toString())
                Result.success(response.data!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun deleteAccount(): Result<DeleteMyAccountMutation.Data?> {
        return try {
            withContext(Dispatchers.IO) {
                val response: ApolloResponse<DeleteMyAccountMutation.Data> =
                    apolloClient.mutation(DeleteMyAccountMutation())
                        .execute()

                if (response.hasErrors()) {
                    val errorMessage = response.errors?.first()?.message ?: "Unknown error"
                    Result.failure(Exception(errorMessage))
                } else {
                    Result.success(response.data)
                }
            }
        } catch (e: ApolloException) {
            Result.failure(e)
        }
    }


}