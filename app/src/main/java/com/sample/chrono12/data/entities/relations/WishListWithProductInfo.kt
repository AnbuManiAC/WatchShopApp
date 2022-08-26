package com.sample.chrono12.data.entities.relations

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Relation
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.WishList

data class WishListWithProductInfo(
    @Embedded val wishList: WishList,
    @Relation(
        parentColumn = "productId",
        entity = Product::class,
        entityColumn = "productId"
    )
    val productWithBrandAndImagesList: ProductWithBrandAndImages
)
