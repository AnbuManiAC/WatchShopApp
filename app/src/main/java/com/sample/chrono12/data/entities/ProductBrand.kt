package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductBrand(
    @PrimaryKey
    val productBrandId: Int,
    val productBrandName: String,
    val productBrandImae: String?
)
