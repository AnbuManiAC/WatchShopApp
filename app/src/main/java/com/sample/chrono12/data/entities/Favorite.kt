package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    val favoriteId: Int = 0,
    val userId: Int,
    val productId: Int
)
