package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    var name: String,
    var emailId: String,
    var password: String,
    var mobileNumber: Int
)
