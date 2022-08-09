package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Order(
    @PrimaryKey(autoGenerate = true)
    val orderId: Int = 0,
    val userId: Int,
    val productCount: Int,
    val orderDateTime: Long,
    val totalPrice: Float,
    val addressId: Int
)
