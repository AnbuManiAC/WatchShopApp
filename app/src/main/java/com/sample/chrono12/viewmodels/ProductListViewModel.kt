package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    var isFirstInitialize = true
    private val subCategoryList = MutableLiveData<List<SubCategory>>()
    private val brandList = MutableLiveData<List<ProductBrand>>()
    private val _watchList = MutableLiveData<List<ProductWithBrandAndImages>>()
    val watchList: LiveData<List<ProductWithBrandAndImages>>
        get() = _watchList

    private val _topRatedWatchList = MutableLiveData<List<ProductWithBrandAndImages>>()
    val topRatedWatchList: LiveData<List<ProductWithBrandAndImages>>
        get() = _topRatedWatchList

    private var productListTitle: String? = null

    private val _sortType =  MutableLiveData<SortType>()
    val sortType: LiveData<SortType>
    get() = _sortType

    private val _searchStatus = MutableLiveData<Int>()
    val searchStatus: LiveData<Int>
        get() = _searchStatus

    private var _searchText : String = ""
    val searchText: String
        get() = _searchText

    init {
        _sortType.value = RATING_HIGH_TO_LOW
        _searchStatus.value = SEARCH_NOT_INITIATED
    }


    private fun setSortType(sortType: SortType) {
        _sortType.value = sortType
    }

    private fun sortProductList(
        watchList: List<ProductWithBrandAndImages>,
        sortType: SortType
    ): List<ProductWithBrandAndImages> {
        val sortResult : List<ProductWithBrandAndImages>
        when (sortType) {
            PRICE_LOW_TO_HIGH -> {
                sortResult = watchList.sortedBy {
                    it.productWithBrand.product.currentPrice
                }
                setSortType(PRICE_LOW_TO_HIGH)
            }
            PRICE_HIGH_TO_LOW -> {
                sortResult = watchList.sortedByDescending {
                    it.productWithBrand.product.currentPrice
                }
                setSortType(PRICE_HIGH_TO_LOW)
            }
            RATING_LOW_TO_HIGH -> {
                sortResult = watchList.sortedBy {
                    it.productWithBrand.product.totalRating
                }
                setSortType(RATING_LOW_TO_HIGH)
            }
            RATING_HIGH_TO_LOW -> {
                sortResult = watchList.sortedByDescending {
                    it.productWithBrand.product.totalRating
                }
                setSortType(RATING_HIGH_TO_LOW)
            }
        }
        return sortResult
    }

    fun applySort(sortType: SortType) {
        _watchList.value = sortProductList(_watchList.value!!, sortType)
    }

    fun setSearchStatus() {
        _searchStatus.value = SEARCH_NOT_INITIATED
    }

    fun setSubCategory() {
        viewModelScope.launch { subCategoryList.postValue(watchRepository.getSubcategory()) }
    }

    fun setBrand() {
        viewModelScope.launch { brandList.postValue(watchRepository.getBrand()) }
    }

    fun getSubCategory(): LiveData<List<SubCategory>> = subCategoryList

    fun getBrand(): LiveData<List<ProductBrand>> = brandList

    fun setSubcategoryWithProductList(subCategoryId: Int, sortType: SortType) {
        viewModelScope.launch {
            _watchList.postValue(
                sortProductList(
                    watchRepository.getSubCategoryWithProduct(
                        subCategoryId
                    ).productWithBrandAndImagesList, sortType
                )
            )
        }
    }

    fun setBrandWithProductList(brandId: Int, sortType: SortType) {
        viewModelScope.launch {
            _watchList.postValue(
                sortProductList(
                    watchRepository.getBrandWithProductAndImages(
                        brandId
                    ), sortType
                )
            )
        }
    }

    fun setAllWatches(sortType: SortType) {
        viewModelScope.launch {
            _watchList.postValue(
                sortProductList(
                    watchRepository.getAllWatches(),
                    sortType
                )
            )
        }
    }

    fun setTopRatedWatches(count: Int) {
        viewModelScope.launch {
            _topRatedWatchList.postValue(watchRepository.getTopRatedWatches(count))
        }
    }

    fun setProductListTitle(title: String) {
        productListTitle = title
    }

    fun getProductListTitle(): String = productListTitle.toString()


    private fun setSearchResult(list: List<ProductWithBrandAndImages>, sortType: SortType) {
        _watchList.value = sortProductList(list, sortType)
        _searchStatus.value = SEARCH_COMPLETED
    }

    fun getSearchResult(): LiveData<List<ProductWithBrandAndImages>> {
        return _watchList
    }

    fun setSearchText(searchText: String){
        _searchText = searchText
    }

    fun setProductsWithBrandAndImagesByQuery(searchQuery: String, sortType: SortType) = viewModelScope.launch {
        setSearchText(searchQuery)
        productListTitle = searchQuery
        _searchStatus.value = SEARCH_INITIATED
        val searchList = getQueryAsList(searchQuery)
        watchRepository.getProductWithBrandAndImagesByQuery(searchList).also {
            setSearchResult(it, sortType)
        }
    }

    private fun getQueryAsList(query: String): List<String> {
        val list = query.split(" ", ",", ", ", " ,", "'")
        val searchQuery = ArrayList<String>()
        list.forEach {
            searchQuery.add("%$it%")
        }
        return searchQuery
    }

    fun setFilterResult(
        args1: List<Int>,
        args2: List<Int>,
        args3: List<Int>,
        args4: List<String>,
        args5: List<Pair<Int, Int>>,
        args6: List<Pair<Int, Int>>
    ) {
        viewModelScope.launch {
            var filterResult = watchRepository.getFilterResult(
                args1.ifEmpty { listOf(1, 2, 3) },
                args2.ifEmpty { listOf(4, 5, 6) },
                args3.ifEmpty { listOf(7, 8, 9, 10, 11) },
                args4.ifEmpty {
                    listOf(
                        "Fastrack",
                        "Titan",
                        "Sonata",
                        "Timex",
                        "Maxima",
                        "Helix",
                        "Fossil"
                    )
                }
            )

            if (args5.isNotEmpty()) {
                val filterResultSet = mutableSetOf<ProductWithBrandAndImages>()
                args5.forEach { (priceAbove, priceBelow) ->
                    filterResultSet.addAll(
                        filterResult.filter {
                            it.productWithBrand.product.currentPrice >= priceAbove &&
                                    it.productWithBrand.product.currentPrice <= priceBelow
                        }
                    )
                }
                filterResult = filterResultSet.toList()
            }
            if (args6.isNotEmpty()) {
                val filterResultSet = mutableSetOf<ProductWithBrandAndImages>()
                args6.forEach { (ratingAbove, ratingBelow) ->
                    filterResultSet.addAll(
                        filterResult.filter {
                            it.productWithBrand.product.totalRating >= ratingAbove.toFloat() &&
                                    it.productWithBrand.product.totalRating <= ratingBelow.toFloat()
                        }
                    )
                }
                filterResult = filterResultSet.toList()
            }
            _watchList.postValue(filterResult)
        }

    }

    companion object {
        const val SEARCH_COMPLETED = 1
        const val SEARCH_INITIATED = 0
        const val SEARCH_NOT_INITIATED = -1
    }

}