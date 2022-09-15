package com.sample.chrono12.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductOrdered

data class OrderedProductInfo(
    @Embedded val productOrdered: ProductOrdered,
    @Relation(
        parentColumn = "productId",
        entity = Product::class,
        entityColumn = "productId"
    )
    val productWithBrandAndImages: ProductWithBrandAndImages
)
