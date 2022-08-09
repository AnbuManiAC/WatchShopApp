package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cart(
    @PrimaryKey(autoGenerate = true)
    val cartId: Int = 0,
    val userId: Int,
    val productId: Int,
    var quantity: Int = 1
)
