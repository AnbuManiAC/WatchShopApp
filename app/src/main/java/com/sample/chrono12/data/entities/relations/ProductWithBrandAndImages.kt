package com.sample.chrono12.data.entities.relations

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Relation
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductImage

data class ProductWithBrandAndImages(
    @Embedded val productWithBrand: ProductWithBrand,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId"
    )
    val images: List<ProductImage>,
)
