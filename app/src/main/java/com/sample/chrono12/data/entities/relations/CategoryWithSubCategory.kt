package com.sample.chrono12.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.sample.chrono12.data.entities.Category
import com.sample.chrono12.data.entities.SubCategory

data class CategoryWithSubCategory(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val subCategory: List<SubCategory>
)
