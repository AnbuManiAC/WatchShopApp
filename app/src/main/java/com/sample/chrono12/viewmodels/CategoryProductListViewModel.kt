package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import com.sample.chrono12.data.entities.Category
import com.sample.chrono12.data.entities.ProductBrand
import com.sample.chrono12.data.entities.SubCategory
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.data.entities.relations.ProductWithImages
import com.sample.chrono12.data.entities.relations.SubCategoryWithProduct
import com.sample.chrono12.data.repository.WatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryProductListViewModel @Inject constructor(
    private val watchRepository: WatchRepository
): ViewModel() {

    private val productWithBrandAndImagesList = MutableLiveData<SubCategoryWithProduct>()
    private val categoryList = MutableLiveData<List<Category>>()
    private val subCategoryList = MutableLiveData<List<SubCategory>>()

    private val brandList = MutableLiveData<List<ProductBrand>>()
    private val brandWithProductList = MutableLiveData<List<ProductWithBrandAndImages>>()

    private var productListTitle: String? = null

    init {
        setCategory()
        setSubCategory()
        setBrand()
    }


    fun setCategory() {
        viewModelScope.launch { categoryList.postValue(watchRepository.getCategory()) }
    }

    fun setSubCategory() {
        viewModelScope.launch { subCategoryList.postValue(watchRepository.getSubcategory()) }
    }

    fun setBrand() {
        viewModelScope.launch { brandList.postValue(watchRepository.getBrand()) }
    }

    fun getCategory(): LiveData<List<Category>> = categoryList

    fun getSubCategory(): LiveData<List<SubCategory>> = subCategoryList

    fun getBrand(): LiveData<List<ProductBrand>> = brandList

    fun setProductWithBrandAndImagesList(subCategoryId: Int){
        viewModelScope.launch { productWithBrandAndImagesList.postValue(watchRepository.getSubCategoryWithProduct(subCategoryId)) }
    }

    fun setBrandWithProductList(brandId: Int){
        viewModelScope.launch { brandWithProductList.postValue(watchRepository.getBrandWithProductAndImages(brandId)) }
    }

    fun getProductWithBrandAndImagesList(): LiveData<SubCategoryWithProduct> = productWithBrandAndImagesList

    fun getBrandWithProductList(): LiveData<List<ProductWithBrandAndImages>> = brandWithProductList

    fun setProductListTitle(title: String){
        productListTitle = title
    }

    fun getProductListTitle(): String = productListTitle.toString()

}