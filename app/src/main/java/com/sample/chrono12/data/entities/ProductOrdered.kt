package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.net.IDN

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["orderId", "productId"], unique = true)]
)
data class ProductOrdered(
    @PrimaryKey(autoGenerate = true)
    val productOrderedId: Int = 0,
    val orderId: Int,
    val productId: Int,
    val quantity: Int = 1
)
