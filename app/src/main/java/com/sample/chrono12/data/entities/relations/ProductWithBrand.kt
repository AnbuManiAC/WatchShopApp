package com.sample.chrono12.data.entities.relations

import androidx.annotation.RequiresPermission
import androidx.room.Embedded
import androidx.room.Relation
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductBrand

data class ProductWithBrand(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "brandId",
        entityColumn = "brandId"
    )
    val brand: ProductBrand
)
