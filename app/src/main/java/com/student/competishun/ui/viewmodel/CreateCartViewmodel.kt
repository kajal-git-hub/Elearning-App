package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.student.competishun.curator.CreateCartItemsMutation
import com.student.competishun.curator.FindAllCartItemsQuery
import com.student.competishun.curator.type.CreateCartItemDto
import com.student.competishun.data.repository.CreateCartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCartViewModel @Inject constructor(private val cartRepository: CreateCartRepository) : ViewModel() {
    private val _cartItemsResult = MutableLiveData<Result<CreateCartItemsMutation.Data>>()
    val cartItemsResult: LiveData<Result<CreateCartItemsMutation.Data>> = _cartItemsResult

    fun createCartItems(userId: String, cartItems: List<CreateCartItemDto>) {
        viewModelScope.launch {
            val result = cartRepository.createCartItems(userId, cartItems)
            _cartItemsResult.value = result
        }
    }

    private val _findAllCartItemsResult = MutableLiveData<Result<FindAllCartItemsQuery.Data>>()
    val findAllCartItemsResult: LiveData<Result<FindAllCartItemsQuery.Data>> = _findAllCartItemsResult

    fun findAllCartItems(userId: String) {
        viewModelScope.launch {
            val result = cartRepository.findAllCartItems(userId)
            _findAllCartItemsResult.value = result
        }
    }

}