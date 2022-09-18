package com.sample.chrono12.viewmodels

import android.text.Spannable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilterViewModel : ViewModel() {
    private val _selectedFilterIds = HashSet<Int>()
    private val _selectedFilterIdsCopy = HashSet<Int>()
    val selectedFilterIds: HashSet<Int>
        get() = if (isClearClicked) HashSet() else _selectedFilterIds

    var isClearClicked = false

    private var _selectedFilterPosition: Int = 0
    val selectedFilterPosition: Int
        get() = _selectedFilterPosition

    fun addSelectedFilter(filterId: Int) {
        _selectedFilterIds.add(filterId)
    }

    fun removeSelectedFilter(filterId: Int) {
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