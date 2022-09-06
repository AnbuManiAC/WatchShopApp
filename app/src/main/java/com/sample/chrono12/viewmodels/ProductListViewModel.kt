package com.sample.chrono12.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chrono12.data.entities.Category
import com.sample.chrono12.data.entities.ProductBrand
import com.sample.chrono12.data.entities.SubCategory
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.data.entities.relations.SubCategoryWithProduct
import com.sample.chrono12.data.repository.WatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val watchRepository: WatchRepository
): ViewModel() {

    private val productWithBrandAndImagesList = MutableLiveData<SubCategoryWithProduct>()
    private val categoryList = MutableLiveData<List<Category>>()
    private val subCategoryList = MutableLiveData<List<SubCategory>>()

    private val brandList = MutableLiveData<List<ProductBrand>>()
    private val brandWithProductList = MutableLiveData<List<ProductWithBrandAndImages>>()

    private val topRatedWatchesList = MutableLiveData<List<ProductWithBrandAndImages>>()

    private val allWatchesList = MutableLiveData<List<ProductWithBrandAndImages>>()

    private var productListTitle: String? = null

    private val searchResult = MutableLiveData<List<ProductWithBrandAndImages>>()

    private val searchStatus = MutableLiveData<Int>()

    init {
        setCategory()
        setSubCategory()
        setBrand()
        searchStatus.value = SEARCH_NOT_INITIATED
    }

    fun setSearchStatus(status: Int){
        searchStatus.value = status
    }

    fun getSearchStatus():LiveData<Int> = searchStatus

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

    fun setTopRatedWacthes(count: Int){
        viewModelScope.launch { topRatedWatchesList.postValue(watchRepository.getTopRatedWatches(count)) }
    }

    fun getTopRatedWatches(): LiveData<List<ProductWithBrandAndImages>> = topRatedWatchesList

    fun setProductListTitle(title: String){
        productListTitle = title
    }

    fun getProductListTitle(): String = productListTitle.toString()

    fun setAllWatches(){
        viewModelScope.launch { allWatchesList.postValue(watchRepository.getAllWatches()) }
    }

    fun getAllWatches(): LiveData<List<ProductWithBrandAndImages>> = allWatchesList

    fun setSearchResult(list: List<ProductWithBrandAndImages>){
        searchResult.value = list
        searchStatus.value= SEARCH_COMPLETED
    }

    fun getSearchResult(): LiveData<List<ProductWithBrandAndImages>> = searchResult

    fun setProductsWithBrandAndImagesByQuery(searchQuery: String) = viewModelScope.launch {
        productListTitle = searchQuery
        setSearchStatus(SEARCH_INITIATED)
        val searchList = getQueryAsList(searchQuery)
        watchRepository.getProductWithBrandAndImagesByQuery(searchList).also{
            setSearchResult(it)
            it.forEach {
                Log.d("SEARCH", "${it.productWithBrand.product.productId}")
            }
            Log.d("SEARCH", "${it.size}")
        }
    }

    private fun getQueryAsList(query: String): List<String>{
        val list = query.split(" ",",",", "," ,")
        Log.d("SEARCH", list.toString())
        val searchQuery = ArrayList<String>()
        list.forEach {
            searchQuery.add("%$it%")
        }
        return searchQuery
    }

    companion object{
        const val SEARCH_COMPLETED = 1
        const val SEARCH_INITIATED = 0
        const val SEARCH_NOT_INITIATED = -1
    }

}