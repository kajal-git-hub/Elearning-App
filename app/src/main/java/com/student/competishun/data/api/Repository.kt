package com.student.competishun.data.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.exception.ApolloException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class Repository @Inject constructor(val apolloClient: ApolloClient) {


    suspend fun <D : Query.Data>
            makeApiCall(query: Query<D>): Flow<ApiProcess<D?>> =
        flow {
            emit(ApiProcess.Loading)
            try {
                val response = apolloClient.query(query).execute()
                emit(ApiProcess.Success(response.data))
            } catch (e: ApolloException) {
                emit(ApiProcess.Failure(e.message ?: "Something went wrong"))
            } catch (e: Exception) {
                emit(ApiProcess.Failure(e.message ?: "Something went wrong"))
            }
        }.flowOn(Dispatchers.IO)


}