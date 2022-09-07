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
import com.sample.chrono12.data.models.SortType
import com.sample.chrono12.data.models.SortType.*
import com.sample.chrono12.data.repository.WatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val watchRepository: WatchRepository
) : ViewModel() {

    private val categoryList = MutableLiveData<List<Category>>()
    private val subCategoryList = MutableLiveData<List<SubCategory>>()
    private val brandList = MutableLiveData<List<ProductBrand>>()
    private val watchList = MutableLiveData<List<ProductWithBrandAndImages>>()

    private var productListTitle: String? = null

//    private val productWithBrandAndImagesList = MutableLiveData<SubCategoryWithProduct>()
//    private val brandWithProductList = MutableLiveData<List<ProductWithBrandAndImages>>()
//    private val topRatedWatchesList = MutableLiveData<List<ProductWithBrandAndImages>>()
//    private val searchResult = MutableLiveData<List<ProductWithBrandAndImages>>()

    private var sortType: SortType? = null

    private val _searchStatus = MutableLiveData<Int>()
    val searchStatus: LiveData<Int>
        get() = _searchStatus

    init {
        setCategory()
        setSubCategory()
        setBrand()
        _searchStatus.value = SEARCH_NOT_INITIATED
        Log.d("SEARCH2", "In search not initiated")
    }

    fun clearSortType() {
        sortType = null
    }

    fun getSortType(): SortType? = sortType

    fun sortProductList(sortType: SortType) {
        when (sortType) {
            PRICE_LOW_TO_HIGH -> {
                watchList.value = watchList.value?.sortedBy {
                    it.productWithBrand.product.currentPrice
                }
                watchList.value = watchList.value?.filter {
                    it.productWithBrand.brand.brandName == "Fastrack" &&
                            it.productWithBrand.product.currentPrice in 1000.0..5000.0
                }?.sortedBy {
                    it.productWithBrand.product.currentPrice
                }
                this.sortType = PRICE_LOW_TO_HIGH
            }
            PRICE_HIGH_TO_LOW -> {
                watchList.value = watchList.value?.sortedByDescending {
                    it.productWithBrand.product.currentPrice
                }
                this.sortType = PRICE_HIGH_TO_LOW
            }
            RATING_LOW_TO_HIGH -> {
                watchList.value = watchList.value?.sortedBy {
                    it.productWithBrand.product.totalRating
                }
                this.sortType = RATING_LOW_TO_HIGH
            }
            RATING_HIGH_TO_LOW -> {
                watchList.value = watchList.value?.sortedByDescending {
                    it.productWithBrand.product.totalRating
                }
                this.sortType = RATING_HIGH_TO_LOW
            }
        }
    }

    fun setSearchStatus() {
        _searchStatus.value = SEARCH_NOT_INITIATED
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

    fun setSubcategoryWithProductList(subCategoryId: Int) {
        viewModelScope.launch {
            watchList.postValue(
                watchRepository.getSubCategoryWithProduct(
                    subCategoryId
                ).productWithBrandAndImagesList
            )
        }
    }

    fun setSubcategoryWithProductList(subCategoryIds: List<Int>) {
        viewModelScope.launch { watchList.postValue(watchRepository.getSubCategoryWithProduct().productWithBrandAndImagesList) }
    }

    fun setBrandWithProductList(brandId: Int) {
        viewModelScope.launch {
            watchList.postValue(
                watchRepository.getBrandWithProductAndImages(
                    brandId
                )
            )
        }
    }

    fun getSubcategoryWithProductList(): LiveData<List<ProductWithBrandAndImages>> = watchList

    fun getBrandWithProductList(): LiveData<List<ProductWithBrandAndImages>> = watchList

    fun setTopRatedWacthes(count: Int) {
        viewModelScope.launch { watchList.postValue(watchRepository.getTopRatedWatches(count)) }
    }

    fun getTopRatedWatches(): LiveData<List<ProductWithBrandAndImages>> = watchList

    fun setProductListTitle(title: String) {
        productListTitle = title
    }

    fun getProductListTitle(): String = productListTitle.toString()

    fun setAllWatches() {
        viewModelScope.launch { watchList.postValue(watchRepository.getAllWatches()) }
    }

    fun getAllWatches(): LiveData<List<ProductWithBrandAndImages>> = watchList

    fun setSearchResult(list: List<ProductWithBrandAndImages>) {
//        searchResult.value = list
        watchList.value = list
        _searchStatus.value = SEARCH_COMPLETED
        Log.d("SEARCH2", "In search completed")
    }

    fun getSearchResult(): LiveData<List<ProductWithBrandAndImages>> = watchList

    fun setProductsWithBrandAndImagesByQuery(searchQuery: String) = viewModelScope.launch {
        productListTitle = searchQuery
        _searchStatus.value = SEARCH_INITIATED
        Log.d("SEARCH2", "In search initiated")
        val searchList = getQueryAsList(searchQuery)
        watchRepository.getProductWithBrandAndImagesByQuery(searchList).also {
            setSearchResult(it)
            it.forEach {
                Log.d("SEARCH", "${it.productWithBrand.product.productId}")
            }
            Log.d("SEARCH", "${it.size}")
        }
    }

    private fun getQueryAsList(query: String): List<String> {
        val list = query.split(" ", ",", ", ", " ,")
        Log.d("SEARCH", list.toString())
        val searchQuery = ArrayList<String>()
        list.forEach {
            searchQuery.add("%$it%")
        }
        return searchQuery
    }

    fun setFilterResult(
        args1: List<Int> = listOf(1, 2, 3),
        args2: List<Int> = listOf(4, 5, 6),
        args3: List<Int> = listOf(7, 8, 9, 10, 11),
        args4: List<String> = listOf(
            "Fastrack",
            "Titan",
            "Sonata",
            "Timex",
            "Maxima",
            "Helix",
            "Fossil"
        )
    ) {

        viewModelScope.launch {
            watchList.postValue(watchRepository.getFilterResult(
                if(args1.isEmpty()) listOf(1, 2, 3) else args1,
                if(args2.isEmpty()) listOf(4, 5, 6) else args2,
                if(args3.isEmpty()) listOf(7, 8, 9, 10, 11) else args3,
                if(args4.isEmpty()) listOf("Fastrack", "Titan", "Sonata", "Timex", "Maxima", "Helix", "Fossil") else args4
            ))
        }
    }

    companion object {
        const val SEARCH_COMPLETED = 1
        const val SEARCH_INITIATED = 0
        const val SEARCH_NOT_INITIATED = -1
    }

}