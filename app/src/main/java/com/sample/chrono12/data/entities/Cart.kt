package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["userId", "productId"], unique = true)]
)
data class Cart(
    @PrimaryKey(autoGenerate = true)
    val cartId: Int = 0,
    val userId: Int,
    val productId: Int,
    val quantity: Int = 1
)
