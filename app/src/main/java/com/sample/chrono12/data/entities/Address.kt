package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["contactName", "addressLine1", "addressLine2", "city", "state", "pincode", "contactNumber"],
        unique = true)]
)
data class Address(
    @PrimaryKey(autoGenerate = true)
    val addressId: Int = 0,
    val contactName: String,
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val state: String,
    val pincode: Int,
    val contactNumber: String,
)
