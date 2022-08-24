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
    indices = [Index(value = ["productId", "title", "content"], unique = true)]
)
data class ProductDetail(
    @PrimaryKey(autoGenerate = true)
    val productDetailId: Int = 0,
    val productId: Int,
    val title: String,
    val content: String
)
