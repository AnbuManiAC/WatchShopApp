package com.sample.chrono12.data.entities

import android.icu.number.FractionPrecision
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ProductBrand::class,
            parentColumns = ["id"],
            childColumns = ["brandId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val brandId: Int,
    val originalPrice: Float,
    var currentPrice: Float,
    var totalRating: Float?,
    var stockCount: Int = 0
)
