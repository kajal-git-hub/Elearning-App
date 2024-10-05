package xyz.penpencil.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import xyz.penpencil.competishun.data.api.Gatekeeper
import com.student.competishun.gatekeeper.MyDetailsQuery
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
}