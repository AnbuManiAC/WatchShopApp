package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductReview(
    @PrimaryKey(autoGenerate = true)
    val productReviewId: Int = 0,
    val productId: Int,
    val userId: Int,
    var rating: Float = 0F,
    var review: String?
)
