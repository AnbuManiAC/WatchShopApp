package com.sample.chrono12.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.sample.chrono12.data.entities.Order
import com.sample.chrono12.data.entities.ProductOrdered
import com.sample.chrono12.data.entities.relations.OrderedProductInfo
import com.sample.chrono12.data.models.OrderInfo
import com.sample.chrono12.data.models.OrderStatus
import com.sample.chrono12.data.repository.UserRepository
import com.sample.chrono12.utils.OrderStatusUpdater
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val orderHistory = MutableLiveData<List<OrderInfo>>()
    private val orderDetailList = MutableLiveData<List<Order>>()
    private val orderedProductInfoList = MutableLiveData<List<OrderedProductInfo>>()

    suspend fun insertOrder(order: Order): Int {
        val orderId = viewModelScope.async(Dispatchers.IO) {
            return@async userRepository.insertOrder(order)
        }
        return orderId.await().toInt()
    }

    fun insertProductOrdered(productOrdered: ProductOrdered) {
        viewModelScope.launch { userRepository.insertProductOrdered(productOrdered) }
    }

    fun setOrderHistory(userId: Int) {
        viewModelScope.launch {
            orderHistory.postValue(userRepository.getOrderHistory(userId))
        }
    }

    suspend fun getOrderInfo(bulkOrderId: Int, userId: Int): OrderInfo {
        val orderInfo = viewModelScope.async(Dispatchers.IO) {
            return@async userRepository.getOrderInfo(bulkOrderId, userId)
        }
        return orderInfo.await()
    }

    fun getOrderHistory(): LiveData<List<OrderInfo>> = orderHistory

    fun setOrderDetail(bulkOrderId: Int, userId: Int) {
        viewModelScope.launch {
            orderDetailList.postValue(userRepository.getOrderDetail(bulkOrderId, userId))
        }
    }

    fun getOrderDetail(): LiveData<List<Order>> = orderDetailList

    fun setOrderedProductInfo(orderId: Int) {
        viewModelScope.launch {
            orderedProductInfoList.postValue(userRepository.getOrderedProductInfo(orderId))
        }
    }

    fun getOrderedProductInfo(): LiveData<List<OrderedProductInfo>> = orderedProductInfoList

    fun initOrderStatusUpdate(orderId: Int, bulkOrderId: Int, isBulkOrder: Boolean, workManager: WorkManager) {
        val workRequest1 = OneTimeWorkRequestBuilder<OrderStatusUpdater>()
            .setInputData(workDataOf("orderId" to orderId, "bulkOrderId" to bulkOrderId, "isBulkOrder" to isBulkOrder, "orderStatus" to "IN TRANSIT"))
            .setInitialDelay(10000, TimeUnit.MILLISECONDS)
            .build()
        val workRequest2 = OneTimeWorkRequestBuilder<OrderStatusUpdater>()
            .setInputData(workDataOf("orderId" to orderId, "bulkOrderId" to bulkOrderId, "isBulkOrder" to isBulkOrder, "orderStatus" to "DELIVERED"))
            .setInitialDelay(10000, TimeUnit.MILLISECONDS)
            .build()
        workManager.beginWith(workRequest1).then(workRequest2).enqueue()
    }

}