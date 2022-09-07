package com.sample.chrono12.data.models

data class Filter(
    val name: String,
    val values: Map<Int, String>,
    var selected: Set<Int>
)
