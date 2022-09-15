package com.sample.chrono12.data.models

import androidx.room.ColumnInfo

data class OrderInfo(
    @ColumnInfo(name = "bulkOrderId") val bulkOrderId: Int,
    @ColumnInfo(name = "orderId") val orderId: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "totalPrice") val totalPrice: Int,
    @ColumnInfo(name = "currentPrice") val currentPrice: Int,
    @ColumnInfo(name = "orderCount") val orderCount: Int,
    @ColumnInfo(name = "productCount") val productCount: Int,
)
