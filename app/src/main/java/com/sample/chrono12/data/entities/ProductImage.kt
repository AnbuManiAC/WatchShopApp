package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductImage(
    @PrimaryKey(autoGenerate = true)
    val productImageId: Int = 0,
    val productId: Int,
    val productImageUrl: String,
    val productImageIndex: Int,
    val productImageDescription: String?
)
