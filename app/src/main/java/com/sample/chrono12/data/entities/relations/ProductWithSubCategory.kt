package com.sample.chrono12.data.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductCategory
import com.sample.chrono12.data.entities.SubCategory

data class ProductWithSubCategory(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "id",
        entity = SubCategory::class,
        entityColumn = "id",
        associateBy = Junction(
            value = ProductCategory::class,
            parentColumn = "productId",
            entityColumn = "subCategoryId"
        )
    )
    val subCategories: List<SubCategory>
)
