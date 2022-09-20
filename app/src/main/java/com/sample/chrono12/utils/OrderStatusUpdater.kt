package com.sample.chrono12.utils

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sample.chrono12.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class OrderStatusUpdater @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val orderRepository: UserRepository
) : Worker(
    context,
    workerParams
) {


    override fun doWork(): Result {
//        Toast.makeText(context, "In do work",Toast.LENGTH_SHORT).show()
        val orderId = inputData.getInt("orderId", 0)
        val bulkOrderId = inputData.getInt("bulkOrderId", 0)
        val isBulkOrder = inputData.getBoolean("isBulkOrder", false)
        val orderStatus = inputData.getString("orderStatus")
        orderRepository.changeOrderStatus(orderId, orderStatus!!)
        Log.d("WorkManager", "In do work")
        val title = "Order status update"
        var message = "Order Id $orderId is $orderStatus"
        if(isBulkOrder){
            message = "Order id $orderId from Bulk order id $bulkOrderId is $orderStatus"
        }
        NotificationUtil(context).createNotification(title,message,orderId, bulkOrderId)
        return Result.success(
            workDataOf(
                "orderId" to orderId,
                "bulkOrderId" to bulkOrderId,
                "isBulkOrder" to isBulkOrder
            )
        )
    }


}