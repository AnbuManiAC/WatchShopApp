package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["userId", "groupName"], unique = true)]
)
data class AddressGroup(
    @PrimaryKey(autoGenerate = true)
    val addressGroupId: Int = 0,
    val userId: Int,
    val groupName: String
)
