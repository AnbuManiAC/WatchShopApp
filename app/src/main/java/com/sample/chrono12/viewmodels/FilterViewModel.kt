package com.sample.chrono12.viewmodels

import androidx.lifecycle.ViewModel

class FilterViewModel : ViewModel() {
    private val _selectedFilterIds = HashSet<Int>()
    val selectedFilterIds: HashSet<Int>
        get() = if (isClearClicked) HashSet() else _selectedFilterIds

    var isClearClicked = false

    private var _selectedFilterPosition: Int = 0
    val selectedFilterPosition: Int
        get() = _selectedFilterPosition

    fun addSelectedFilter(filterId: Int) {
        _selectedFilterIds.add(filterId)
    }

    private fun removeSelectedFilter(filterId: Int) {
        _selectedFilterIds.remove(filterId)
    }

    fun addDeleteSelectedFilter(filterId: Int, isAdd: Boolean) {
        if (isAdd) {
            addSelectedFilter(filterId)
        } else {
            removeSelectedFilter(filterId)
        }
    }

    fun clearSelectedFilterIds() {
        _selectedFilterIds.clear()
    }

    fun setSelectedFilterPosition(position: Int) {
        _selectedFilterPosition = position
    }

    fun clearSelectedFilterPosition() {
        _selectedFilterPosition = 0
    }
}