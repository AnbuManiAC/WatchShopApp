package com.sample.chrono12.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.sample.chrono12.data.entities.Cart
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductImage

data class CartWithProductAndImages(
    @Embedded val cart: Cart,
    @Relation(
        parentColumn = "productId",
        entity = Product::class,
        entityColumn = "id"
    )
    val productWithImages: ProductWithImages
)
