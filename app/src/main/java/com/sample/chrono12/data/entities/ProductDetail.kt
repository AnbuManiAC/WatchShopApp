package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import hilt_aggregated_deps._com_sample_chrono12_viewmodels_ProductCategoryViewModel_HiltModules_KeyModule

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
