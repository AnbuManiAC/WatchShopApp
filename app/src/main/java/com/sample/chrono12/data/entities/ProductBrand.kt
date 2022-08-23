package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["brandName"], unique = true),
        Index(value = ["brandImageUrl"], unique = true)
    ]
)
data class ProductBrand(
    @PrimaryKey(autoGenerate = true)
    val brandId: Int = 0,
    val brandName: String,
    val brandImageUrl: String
)
