package com.sample.chrono12.data.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductCategoryCrossRef
import com.sample.chrono12.data.entities.SubCategory

data class SubCategoryWithProduct(
    @Embedded
    val subCategory: SubCategory,
    @Relation(
        parentColumn = "subCategoryId",
        entity = Product::class,
        entityColumn = "productId",
        associateBy = Junction(
            value = ProductCategoryCrossRef::class,
            parentColumn = "subCategoryId",
            entityColumn = "productId"
        )
    )
    val productWithBrandAndImagesList: List<ProductWithBrandAndImages>
)
