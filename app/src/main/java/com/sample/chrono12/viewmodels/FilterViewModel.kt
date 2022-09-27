package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilterViewModel : ViewModel() {
    private val _appliedFilterIds = HashSet<Int>()
    val appliedFilterIds: HashSet<Int>
        get() = _appliedFilterIds

    private val _selectedFilterIds = HashSet<Int>()
    val selectedFilterIds: HashSet<Int>
    get() = if (isClearClicked) HashSet() else _selectedFilterIds

    var isClearClicked = false

    private val _filterCount = MutableLiveData<Int>()
    val filterCount: LiveData<Int>
    get() = _filterCount

    private var _selectedFilterPosition: Int = 0
    val selectedFilterPosition: Int
        get() = _selectedFilterPosition

    private fun addSelectedFilter(filterId: Int) {
        selectedFilterIds.add(filterId)
    }

    private fun removeSelectedFilter(filterId: Int) {
        selectedFilterIds.remove(filterId)
    }

    fun setSelectedFilterIds(filterIds: HashSet<Int>){
        selectedFilterIds.clear()
        selectedFilterIds.addAll(filterIds)
    }

    fun addDeleteSelectedFilter(filterId: Int, isAdd: Boolean) {
        if (isAdd) {
            addSelectedFilter(filterId)
        } else {
            removeSelectedFilter(filterId)
        }
    }

    fun clearSelectedFilterIds() {
        _appliedFilterIds.clear()
        _filterCount.value = 0
    }

    fun setAppliedFilterIds(filterIds: HashSet<Int>){
        _appliedFilterIds.clear()
        _appliedFilterIds.addAll(filterIds)
        _filterCount.value = _appliedFilterIds.size
    }

    fun setAppliedFilterIds(filterId: Int){
        _appliedFilterIds.clear()
        _appliedFilterIds.add(filterId)
        _filterCount.value = _appliedFilterIds.size
    }

    fun setSelectedFilterPosition(position: Int) {
        _selectedFilterPosition = position
    }

    fun clearSelectedFilterPosition() {
        _selectedFilterPosition = 0
    }
}