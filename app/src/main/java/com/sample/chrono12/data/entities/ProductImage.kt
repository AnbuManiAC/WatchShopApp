package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["imageUrl"], unique = true)]
)
data class ProductImage(
    @PrimaryKey(autoGenerate = true)
    val productImageId: Int = 0,
    val productId: Int,
    val imageUrl: String,
    val imageIndex: Int
)
