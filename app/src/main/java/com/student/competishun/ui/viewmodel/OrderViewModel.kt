package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.coinkeeper.CreateOrderMutation
import com.student.competishun.coinkeeper.GenerateReceiptQuery
import com.student.competishun.coinkeeper.type.CreateOrderInput
import com.student.competishun.data.api.Coinkeeper
import com.student.competishun.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor( private val orderRepository: OrderRepository) : ViewModel() {

    private val _orderResult = MutableLiveData<Result<CreateOrderMutation.Data>>()
    val orderResult: LiveData<Result<CreateOrderMutation.Data>> = _orderResult

    fun createOrder(input: CreateOrderInput) {
        viewModelScope.launch {
            val result = orderRepository.createOrder(input)
            _orderResult.value = result
        }
    }
}