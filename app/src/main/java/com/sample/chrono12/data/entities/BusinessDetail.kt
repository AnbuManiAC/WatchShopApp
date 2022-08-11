package com.sample.chrono12.data.entities

import android.icu.number.FractionPrecision
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BusinessDetail(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val GSTIN: String,
    val businessName: String,
    val entityType: String,
    val businessType: String
)
