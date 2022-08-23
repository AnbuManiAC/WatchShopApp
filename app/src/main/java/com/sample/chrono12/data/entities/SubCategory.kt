package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["categoryId", "name"], unique = true),
        Index(value = ["imageUrl"], unique = true)
    ]
)
data class SubCategory(
    @PrimaryKey(autoGenerate = true)
    val subCategoryId: Int,
    val categoryId: Int,
    val name: String,
    val imageUrl: String,
)
