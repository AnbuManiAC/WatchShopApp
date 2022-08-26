package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["mobileNumber"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val name: String,
    val email: String,
    val mobileNumber: String,
    val password: String,
    val image: String? = null
)
