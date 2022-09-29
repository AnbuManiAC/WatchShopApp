package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.sample.chrono12.data.entities.Order
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductOrdered
import com.sample.chrono12.data.entities.relations.OrderedProductInfo
import com.sample.chrono12.data.models.OrderInfo
import com.sample.chrono12.data.models.RandomNumber
import com.sample.chrono12.data.repository.UserRepository
import com.sample.chrono12.utils.OrderStatusUpdater
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val orderHistory = MutableLiveData<List<OrderInfo>>()
//    private val orderDetailList = MutableLiveData<List<Order>>()
    private val orderedProductInfoList = MutableLiveData<List<OrderedProductInfo>>()

    suspend fun insertOrder(order: Order): Int  =
            userRepository.insertOrder(order).toInt()
//        val orderId = viewModelScope.async(Dispatchers.IO) {
//            return@async userRepository.insertOrder(order)
//        }
//        return orderId.await().toInt()
//    }

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

//    fun setOrderDetail(bulkOrderId: Int, userId: Int) {
//        viewModelScope.launch {
//            orderDetailList.postValue(userRepository.getOrderDetail(bulkOrderId, userId))
//        }
//    }

//    fun getOrderDetail(): LiveData<List<Order>> = orderDetailList

    fun getOrderDetail(bulkOrderId: Int, userId: Int): LiveData<List<Order>> = userRepository.getOrderDetail(bulkOrderId, userId)

    fun setOrderedProductInfo(orderId: Int) {
        viewModelScope.launch {
            orderedProductInfoList.postValue(userRepository.getOrderedProductInfo(orderId))
        }
    }

    fun getOrderedProductInfo(): LiveData<List<OrderedProductInfo>> = orderedProductInfoList

    fun initOrderStatusUpdate(
        orderId: Int,
        bulkOrderId: Int,
        isBulkOrder: Boolean,
        workManager: WorkManager
    ) {
        val workData1 = workDataOf(
            "orderId" to orderId,
            "bulkOrderId" to bulkOrderId,
            "isBulkOrder" to isBulkOrder,
            "orderStatus" to "IN TRANSIT"
        )
        val workData2 = workDataOf(
            "orderId" to orderId,
            "bulkOrderId" to bulkOrderId,
            "isBulkOrder" to isBulkOrder,
            "orderStatus" to "DELIVERED"
        )
        val workRequest1 = OneTimeWorkRequestBuilder<OrderStatusUpdater>()
            .setInputData(workData1)
//            .setInitialDelay(
//                Random.nextInt(RandomNumber.LOW.value, RandomNumber.HIGH.value).toLong(),
//                TimeUnit.MINUTES
//            )
            .setInitialDelay(10000, TimeUnit.MILLISECONDS)
            .build()
        val workRequest2 = OneTimeWorkRequestBuilder<OrderStatusUpdater>()
            .setInputData(workData2)
//            .setInitialDelay(
//                Random.nextInt(RandomNumber.LOW.value, RandomNumber.HIGH.value).toLong(),
//                TimeUnit.MINUTES
//            )
            .setInitialDelay(10000, TimeUnit.MILLISECONDS)
            .build()
        workManager.beginWith(workRequest1).then(workRequest2).enqueue()
    }

    fun updateProductStock(productId: Int, stockCount: Int) {
        viewModelScope.launch {
            userRepository.updateStockCount(productId, stockCount)
        }
    }

}