package com.sample.chrono12.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductBrand

data class BrandWithProductAndImages(
    @Embedded val brand: ProductBrand,
    @Relation(
        parentColumn = "id",
        entity = Product::class,
        entityColumn = "brandId"
    )
    val productWithImages: List<ProductWithImages>
)
