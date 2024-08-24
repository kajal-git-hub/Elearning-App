package com.student.competishun.data.repository

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.coinkeeper.OrdersByUserIdsQuery
import com.student.competishun.data.api.Coinkeeper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrdersRepository @Inject constructor(@Coinkeeper private val apolloClient: ApolloClient) {

    suspend fun getOrdersByUserIds(userIds: List<String>): List<OrdersByUserIdsQuery.OrdersByUserId>? {
        val query = OrdersByUserIdsQuery(userIds)

        return try {
            val response = apolloClient.query(query).execute()

            if (response.hasErrors()) {
                response.errors?.forEach {
                    Log.e("GraphQL Error", it.message)
                }
                return null
            }

            response.data?.ordersByUserIds
        } catch (e: ApolloException) {
            Log.e("ApolloException", e.message ?: "Unknown error")
            null
        }
    }
}
