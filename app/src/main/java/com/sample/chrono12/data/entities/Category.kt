package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["categoryName"], unique = true)]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    val categoryName: String
)
