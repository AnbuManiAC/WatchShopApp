package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["bulkOrderId", "userId", "timestamp", "addressInfo"], unique = true)]
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val orderId: Int = 0,
    val bulkOrderId: Int,
    val userId: Int,
    val timestamp: Long,
    val actualTotal: Float,
    val totalPrice: Float,
    val addressInfo: String,
    val orderStatus: String
)
