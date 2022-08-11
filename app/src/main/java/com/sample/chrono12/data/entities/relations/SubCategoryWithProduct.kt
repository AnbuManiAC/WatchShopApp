package com.sample.chrono12.data.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductCategory
import com.sample.chrono12.data.entities.SubCategory

data class SubCategoryWithProduct(
    @Embedded
    val subCategory: SubCategory,
    @Relation(
        parentColumn = "id",
        entity = Product::class,
        entityColumn = "id",
        associateBy = Junction(
            value = ProductCategory::class,
            parentColumn = "subCategoryId",
            entityColumn = "productId"
        )
    )
    val products: List<Product>
)
