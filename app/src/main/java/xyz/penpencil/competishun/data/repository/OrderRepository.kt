package xyz.penpencil.competishun.data.repository

import com.apollographql.apollo3.ApolloClient
import com.student.competishun.coinkeeper.CreateOrderMutation
import com.student.competishun.coinkeeper.type.CreateOrderInput
import xyz.penpencil.competishun.data.api.Coinkeeper
import javax.inject.Inject

class OrderRepository @Inject constructor(@Coinkeeper private val apolloClient: ApolloClient) {

    suspend fun createOrder(input: CreateOrderInput): Result<CreateOrderMutation.Data> {
        return try {
            val response = apolloClient.mutation(CreateOrderMutation(input)).execute()
            if (response.hasErrors()) {
                Result.failure(Exception(response.errors?.firstOrNull()?.message))
            } else {
                Result.success(response.data!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}