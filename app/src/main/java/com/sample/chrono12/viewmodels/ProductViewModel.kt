package com.sample.chrono12.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.repository.WatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val watchRepository: WatchRepository
): ViewModel() {

    val product = MutableLiveData<List<Product>>()

    fun setProduct() {
        viewModelScope.launch {
            product.postValue(watchRepository.getProducts())
        }
    }
}