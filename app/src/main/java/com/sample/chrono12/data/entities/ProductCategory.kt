package com.sample.chrono12.data.entities

import androidx.room.Entity

@Entity(primaryKeys = ["subCategoryId", "productId"])
data class ProductCategory(
    val subCategoryId: Int,
    val productId: Int
)
