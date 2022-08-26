package com.sample.chrono12.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.sample.chrono12.data.entities.Cart
import com.sample.chrono12.data.entities.Product

data class CartWithProductInfo(
    @Embedded val cart: Cart,
    @Relation(
        parentColumn = "productId",
        entity = Product::class,
        entityColumn = "productId"
    )
    val productWithBrandAndImagesList: List<ProductWithBrandAndImages>
)
