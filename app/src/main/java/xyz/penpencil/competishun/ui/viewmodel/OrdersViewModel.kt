package xyz.penpencil.competishun.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.competishun.coinkeeper.CoursePaymentsByUserIdQuery
import com.student.competishun.coinkeeper.GenerateReceiptQuery
import com.student.competishun.coinkeeper.OrdersByUserIdsQuery
import xyz.penpencil.competishun.data.repository.OrdersRepository
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

    private val _paymentBreakdown = MutableLiveData<List<CoursePaymentsByUserIdQuery.CoursePaymentsByUserId>?>()
    val paymentResult: LiveData<List<CoursePaymentsByUserIdQuery.CoursePaymentsByUserId>?> get() = _paymentBreakdown

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

    fun getPaymentBreakdown(courseId:String, userId:String){
        viewModelScope.launch {
            val result = ordersRepository.getCoursePaymentsByUserId(courseId,userId)
            _paymentBreakdown.value  = result

        }
    }
}
