package com.sample.chrono12.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductOrdered

data class ProductOrderedWithProductAndImages(
    @Embedded val productOrdered: ProductOrdered,
    @Relation(
        parentColumn = "productId",
        entity = Product::class,
        entityColumn = "id"
    )
    val productWithImages: ProductWithImages
)
