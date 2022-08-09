package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SubCategory(
    @PrimaryKey(autoGenerate = true)
    val subCategoryId: Int,
    val categoryId: Int,
    val name: String,
    val image: String
)
