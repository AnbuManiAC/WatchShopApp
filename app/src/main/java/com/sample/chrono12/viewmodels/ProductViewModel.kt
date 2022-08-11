package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductDetail
import com.sample.chrono12.data.entities.relations.*
import com.sample.chrono12.data.repository.WatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val watchRepository: WatchRepository
): ViewModel() {

    private val product = MutableLiveData<Product>()
    private val productList = MutableLiveData<List<Product>>()
    private val productImage = MutableLiveData<List<String>>()
    private val productDetail = MutableLiveData<List<ProductDetail>>()



}