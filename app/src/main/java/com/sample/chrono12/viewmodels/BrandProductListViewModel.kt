package com.sample.chrono12.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.chrono12.data.entities.ProductBrand
import com.sample.chrono12.data.entities.relations.BrandWithProductAndImages
import com.sample.chrono12.data.entities.relations.ProductWithBrand
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.data.repository.WatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BrandProductListViewModel @Inject constructor(
    private val watchRepository: WatchRepository
) : ViewModel() {

    private val productWithBrandAndImagesList = MutableLiveData<List<ProductWithBrandAndImages>>()
    private val brandList = MutableLiveData<List<ProductBrand>>()
    private var productListTitle: String? = null

}