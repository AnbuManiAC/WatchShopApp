package com.sample.chrono12.data.entities

import androidx.fragment.app.Fragment
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = AddressGroup::class,
        parentColumns = ["id"],
        childColumns = ["groupId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.SET_NULL
    )]
)
data class Address(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val groupId: Int,
    val userId: Int,
    var addressLine1: String,
    var addressLine2: String,
    var pincode: Int
)
