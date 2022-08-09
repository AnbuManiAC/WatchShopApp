package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Address(
    @PrimaryKey(autoGenerate = true)
    val addressId: Int = 0,
    val userId: Int,
    var addressLine1: String,
    var addressLine2: String,
    var pincode: Int
)
