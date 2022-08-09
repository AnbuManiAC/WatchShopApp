package com.sample.chrono12.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class SearchSuggestion(
    @PrimaryKey(autoGenerate = true)
    val searchSuggestionId: Int = 0,
    val userId: Int = 0,
    val suggestion: String
)
