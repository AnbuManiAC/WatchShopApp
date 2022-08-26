package com.sample.chrono12.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chrono12.data.entities.ProductBrand
import com.sample.chrono12.data.entities.ProductDetail
import com.sample.chrono12.data.entities.relations.*
import com.sample.chrono12.data.repository.WatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val watchRepository: WatchRepository,
) : ViewModel() {

    private val product = MutableLiveData<ProductWithBrandAndImages>()
    private val productDetail = MutableLiveData<List<ProductDetail>>()
    private val isProductInUserWishList = MutableLiveData<Boolean>()

    init {
        isProductInUserWishList.value = false
    }


    fun setProduct(productId: Int) {
        viewModelScope.launch {
            Log.d("dataaa",
                watchRepository.getProductWithImages(productId).productImage[0].imageUrl)
            product.postValue(watchRepository.getProductWithBrandAndImages(productId))
            productDetail.postValue(watchRepository.getProductDetail(productId))
        }
    }

    fun getProduct(): LiveData<ProductWithBrandAndImages> = product

    fun getProductDetail(): LiveData<List<ProductDetail>> = productDetail

    fun setIsProductInUserWishList(isWishlisted: Boolean){
        isProductInUserWishList.value = isWishlisted
    }

    fun getIsProductInUserWishList():LiveData<Boolean> = isProductInUserWishList

}