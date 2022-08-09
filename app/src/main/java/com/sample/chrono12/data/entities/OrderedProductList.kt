package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderedProductList(
    @PrimaryKey(autoGenerate = true)
    val OrderedProductListId: Int = 0,
    val orderId: Int,
    val productId: Int,
    val quantity: Int = 1
)
