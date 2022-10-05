package com.sample.chrono12.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.sample.chrono12.R
import com.sample.chrono12.ui.fragment.OrderDetailFragmentArgs

class NotificationUtil(val context: Context) {

    companion object{
        private const val channelId = "Channel id"
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelId,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(title: String, message: String, orderId: Int, bulkOrderId: Int) {

        createNotificationChannel()

        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.home_nav)
            .setDestination(R.id.orderDetailFragment)
            .setArguments(
                OrderDetailFragmentArgs(bulkOrderId, orderId).toBundle()
            )
            .createPendingIntent()

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()


        NotificationManagerCompat.from(context).notify(getNotificationId(), notification)

    }

    private fun getNotificationId():Int{
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.user_pref),
            Context.MODE_PRIVATE
        )
        var notificationId = sharedPref.getInt(context.getString(R.string.notification_id), 0)
        notificationId++
        val editor = sharedPref?.edit()
        editor?.let {
            editor.putInt(context.getString(R.string.notification_id), notificationId)
            editor.apply()
        }
        return notificationId
    }
}