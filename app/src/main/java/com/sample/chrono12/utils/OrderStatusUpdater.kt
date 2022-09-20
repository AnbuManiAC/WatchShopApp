package com.sample.chrono12.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.contentValuesOf
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.ViewModelProvider
import androidx.room.Database
import androidx.work.*
import com.sample.chrono12.R
import com.sample.chrono12.data.models.OrderStatus
import com.sample.chrono12.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject
import javax.inject.Provider

@HiltWorker
class OrderStatusUpdater @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val orderRepository: UserRepository
) : CoroutineWorker(
    context,
    workerParams
) {


    override suspend fun doWork(): Result {
        Toast.makeText(context, "In do work",Toast.LENGTH_SHORT).show()
        val orderId = inputData.getInt("orderId", 0)
        val bulkOrderId = inputData.getInt("bulkOrderId", 0)
        val isBulkOrder = inputData.getBoolean("isBulkOrder", false)
        orderRepository.changeOrderStatus(orderId, OrderStatus.IN_TRANSIT)
        Log.d("Workmanager", "In do work")
        return Result.success(
            workDataOf(
                "orderId" to orderId,
                "bulkOrderId" to bulkOrderId,
                "isBulkOrder" to isBulkOrder
            )
        )
    }

    private fun createNotification(title: String, description: String) {

        var notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("101", "channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "101")
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.mipmap.ic_app_icon)
            .build()

        notificationManager.notify(1, notification)
    }
}