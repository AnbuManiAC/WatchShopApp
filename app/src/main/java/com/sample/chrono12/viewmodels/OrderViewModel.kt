package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.relations.*
import com.sample.chrono12.data.repository.WatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val watchRepository: WatchRepository
): ViewModel() {

    private var product = MutableLiveData<Product>()
    private var categoryWithSubCategory = MutableLiveData<List<CategoryWithSubCategory>>()
    private var productWithImages = MutableLiveData<List<ProductWithImages>>()
    private var productWithSubCategory = MutableLiveData<List<ProductWithSubCategory>>()
    private var subCategoryWithProduct = MutableLiveData<List<SubCategoryWithProduct>>()
    private var cartWithProductAndImages = MutableLiveData<List<CartWithProductAndImages>>()
    private var productOrderedWithProductAndImages = MutableLiveData<List<ProductOrderedWithProductAndImages>>()
    private var brandWithProductAndImages = MutableLiveData<List<BrandWithProductAndImages>>()

    fun setProduct(id: Int) {
        viewModelScope.launch {
            product.postValue(watchRepository.getProduct(id))
        }
    }

    fun getProduct(): LiveData<Product> = product

    fun setCategoryWithSubCategory(){
        viewModelScope.launch {
            categoryWithSubCategory.postValue(watchRepository.getCategoryWithSubCategory())
        }
    }

    fun getCategoryWithSubCategory(): LiveData<List<CategoryWithSubCategory>> = categoryWithSubCategory

    fun setProductWithImages(){
        viewModelScope.launch {
            productWithImages.postValue(watchRepository.getProductWithImages())
        }
    }

    fun getProductWithImages(): LiveData<List<ProductWithImages>> = productWithImages


    fun setProductWithSubCategory(){
        viewModelScope.launch {
            productWithSubCategory.postValue(watchRepository.getProductWithSubCategory())
        }
    }

    fun getProductWithSubCategory(): LiveData<List<ProductWithSubCategory>> = productWithSubCategory

    fun setSubCategoryWithProduct(){
        viewModelScope.launch {
            subCategoryWithProduct.postValue(watchRepository.getSubCategoryWithProduct())
        }
    }

    fun getSubCategoryWithProduct(): LiveData<List<SubCategoryWithProduct>> = subCategoryWithProduct

    fun setCartWithProductAndImages(){
        viewModelScope.launch {
            cartWithProductAndImages.postValue(watchRepository.getCartWithProductAndImages())
        }
    }

    fun getCartWithProductAndImages(): LiveData<List<CartWithProductAndImages>> = cartWithProductAndImages

    fun setProductOrderedWithProductAndImages(){
        viewModelScope.launch {
            productOrderedWithProductAndImages.postValue(watchRepository.getProductOrderedWithProductAndImages())
        }
    }

    fun getProductOrderedWithProductAndImages(): LiveData<List<ProductOrderedWithProductAndImages>> = productOrderedWithProductAndImages

    fun setBrandWithProductAndImages(){
        viewModelScope.launch {
            brandWithProductAndImages.postValue(watchRepository.getBrandWithProductAndImages())
        }
    }

    fun getBrandWithProductAndImages(): LiveData<List<BrandWithProductAndImages>> = brandWithProductAndImages
}