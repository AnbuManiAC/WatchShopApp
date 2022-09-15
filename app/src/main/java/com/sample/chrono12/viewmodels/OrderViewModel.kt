package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chrono12.data.entities.Order
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductOrdered
import com.sample.chrono12.data.entities.relations.*
import com.sample.chrono12.data.models.OrderInfo
import com.sample.chrono12.data.repository.UserRepository
import com.sample.chrono12.data.repository.WatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val orderHistory = MutableLiveData<List<OrderInfo>>()
    private val orderDetailList = MutableLiveData<List<Order>>()
    private val orderedProductInfoList = MutableLiveData<List<OrderedProductInfo>>()

    suspend fun insertOrder(order: Order):Int {
        val orderId = viewModelScope.async(Dispatchers.IO) {
            return@async userRepository.insertOrder(order)
        }
        return orderId.await().toInt()
    }

    fun insertProductOrdered(productOrdered: ProductOrdered) {
        viewModelScope.launch { userRepository.insertProductOrdered(productOrdered) }
    }

    fun setOrderHistory(userId: Int){
        viewModelScope.launch {
            orderHistory.postValue(userRepository.getOrderHistory(userId))
        }
    }

    suspend fun getOrderInfo(bulkOrderId: Int, userId: Int): OrderInfo{
        val orderInfo = viewModelScope.async(Dispatchers.IO) {
             return@async userRepository.getOrderInfo(bulkOrderId, userId)
        }
        return orderInfo.await()
    }

    fun getOrderHistory(): LiveData<List<OrderInfo>> = orderHistory

    fun setOrderDetail(bulkOrderId: Int, userId: Int){
        viewModelScope.launch {
            orderDetailList.postValue(userRepository.getOrderDetail(bulkOrderId, userId))
        }
    }

    fun getOrderDetail(): LiveData<List<Order>> = orderDetailList

    fun setOrderedProductInfo(orderId: Int){
        viewModelScope.launch {
            orderedProductInfoList.postValue(userRepository.getOrderedProductInfo(orderId))
        }
    }

    fun getOrderedProductInfo(): LiveData<List<OrderedProductInfo>> = orderedProductInfoList

}