package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["subCategoryId", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = SubCategory::class,
            parentColumns = ["subCategoryId"],
            childColumns = ["subCategoryId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProductCategoryCrossRef(
    val subCategoryId: Int,
    val productId: Int,
)
