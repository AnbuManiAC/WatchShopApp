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
    indices = [
        Index(value = ["userId", "suggestion"], unique = true)
    ]
)
data class SearchSuggestion(
    @PrimaryKey(autoGenerate = true)
    val searchSuggestionId: Int = 0,
    val userId: Int = 0,
    val suggestion: String,
    val timestamp: Long
)
