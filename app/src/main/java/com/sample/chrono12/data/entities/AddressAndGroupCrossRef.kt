package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["addressGroupId", "addressId"],
    foreignKeys = [
        ForeignKey(
            entity = AddressGroup::class,
            parentColumns = ["addressGroupId"],
            childColumns = ["addressGroupId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Address::class,
            parentColumns = ["addressId"],
            childColumns = ["addressId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE)
    ]
)
data class AddressAndGroupCrossRef(
    val addressGroupId: Int,
    val addressId: Int,
)
