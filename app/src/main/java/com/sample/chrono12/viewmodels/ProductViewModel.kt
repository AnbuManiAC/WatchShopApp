package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chrono12.data.entities.ProductDetail
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
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
    private val isProductInUserCart = MutableLiveData<Boolean>()

    init {
        isProductInUserWishList.value = false
    }


    fun setProduct(productId: Int) {
        viewModelScope.launch {
            product.postValue(watchRepository.getProductWithBrandAndImages(productId))
            productDetail.postValue(watchRepository.getProductDetail(productId))
        }
    }

    fun getProduct(): LiveData<ProductWithBrandAndImages> = product

    fun getProductDetail(): LiveData<List<ProductDetail>> = productDetail

    fun setIsProductInUserWishList(isWishListed: Boolean){
        isProductInUserWishList.value = isWishListed
    }

    fun getIsProductInUserWishList():LiveData<Boolean> = isProductInUserWishList

    fun setIsProductInUserCart(isProductInCart: Boolean){
        isProductInUserCart.value = isProductInCart
    }

    fun getIsProductInUserCart():LiveData<Boolean> = isProductInUserCart

}