package com.sample.chrono12.data.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductCategoryCrossRef
import com.sample.chrono12.data.entities.SubCategory

data class ProductWithSubCategory(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "productId",
        entity = SubCategory::class,
        entityColumn = "subCategoryId",
        associateBy = Junction(
            value = ProductCategoryCrossRef::class,
            parentColumn = "productId",
            entityColumn = "subCategoryId"
        )
    )
    val subCategories: List<SubCategory>
)
