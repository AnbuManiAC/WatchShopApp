package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductDetail(
    @PrimaryKey(autoGenerate = true)
    val productDetailId: Int = 0,
    val productId: Int,
    val productDetailTitle: String,
    val productDetailContent: String
)
