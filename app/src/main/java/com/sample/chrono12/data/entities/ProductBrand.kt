package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductBrand(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val brandName: String,
    val brandImage: String?
)
