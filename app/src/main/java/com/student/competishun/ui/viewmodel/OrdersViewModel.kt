package com.student.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.coinkeeper.GenerateReceiptQuery
import com.student.competishun.coinkeeper.OrdersByUserIdsQuery
import com.student.competishun.data.repository.OrdersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val ordersRepository: OrdersRepository
) : ViewModel() {

    private val _ordersByUserIds = MutableLiveData<List<OrdersByUserIdsQuery.OrdersByUserId>?>()
    val ordersByUserIds: LiveData<List<OrdersByUserIdsQuery.OrdersByUserId>?> get() = _ordersByUserIds

    private val _receiptResult = MutableLiveData<Result<GenerateReceiptQuery.Data>>()
    val receiptResult: LiveData<Result<GenerateReceiptQuery.Data>> = _receiptResult

    fun fetchOrdersByUserIds(userIds: List<String>) {
        viewModelScope.launch {
            _ordersByUserIds.value = ordersRepository.getOrdersByUserIds(userIds)
        }
    }

    fun generateReceipt(transactionId: String) {
        viewModelScope.launch {
            val result = ordersRepository.generateReceipt(transactionId)
            _receiptResult.value = result
        }
    }
}
