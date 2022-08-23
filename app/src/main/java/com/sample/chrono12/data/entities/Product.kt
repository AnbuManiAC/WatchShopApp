package com.sample.chrono12.data.entities

import android.icu.number.FractionPrecision
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ProductBrand::class,
            parentColumns = ["brandId"],
            childColumns = ["brandId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["name"], unique = true)]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val productId: Int = 0,
    val name: String,
    val brandId: Int,
    val originalPrice: Float,
    val currentPrice: Float,
    val totalRating: Float?,
    val stockCount: Int = 0
)
