package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(
    @PrimaryKey
    val productId: Int,
    val name: String,
    val brandId: Int,
    val originalPrice: Float,
    var currentPrice: Float,
    var totalRating: Float?,
    var stockCount: Int = 0
)
