package com.sample.chrono12.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductImage

data class ProductWithImages(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    )
    val productImage: List<ProductImage>
)
