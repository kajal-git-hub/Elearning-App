package com.student.competishun.data.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.curator.CreateCartItemsMutation
import com.student.competishun.curator.FindAllCartItemsQuery
import com.student.competishun.curator.RemoveCartMutation
import com.student.competishun.curator.type.CreateCartItemDto
import com.student.competishun.curator.type.CreateCartItemsDto
import com.student.competishun.data.api.Curator
import javax.inject.Inject

class CreateCartRepository@Inject constructor(@Curator private val apolloClient: ApolloClient)  {

    suspend fun createCartItems(userId: String, cartItems: List<CreateCartItemDto>): Result<CreateCartItemsMutation.Data> {
        return try {
            val input = CreateCartItemsDto(cartItems)
            val response = apolloClient.mutation(CreateCartItemsMutation(input)).execute()
            if (response.hasErrors()) {
                Result.failure(Exception(response.errors?.first()?.message))
            } else {
                Result.success(response.data!!)
            }
        } catch (e: ApolloException) {
            Result.failure(e)
        }
    }

    suspend fun removeCart(removeCartId: String): Result<Unit> {
        return try {
            val response = apolloClient.mutation(RemoveCartMutation(removeCartId)).execute()

            if (response.hasErrors()) {
                Result.failure(Exception(response.errors?.firstOrNull()?.message))
            } else {
                Result.success(Unit) // Return success without any data
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun findAllCartItems(userId: String): Result<FindAllCartItemsQuery.Data> {
        return try {
            val response = apolloClient.query(FindAllCartItemsQuery()).execute()
            if (response.hasErrors()) {
                Result.failure(Exception(response.errors?.first()?.message))
            } else {
                Result.success(response.data!!)
            }
        } catch (e: ApolloException) {
            Result.failure(e)
        }
    }
}