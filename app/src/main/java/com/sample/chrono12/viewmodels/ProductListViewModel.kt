package com.sample.chrono12.viewmodels

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

    private var sortType: SortType? = null

    private val _searchStatus = MutableLiveData<Int>()
    val searchStatus: LiveData<Int>
        get() = _searchStatus

    init {
        setCategory()
        setSubCategory()
        setBrand()
        _searchStatus.value = SEARCH_NOT_INITIATED
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

    private fun setCategory() {
        viewModelScope.launch { categoryList.postValue(watchRepository.getCategory()) }
    }

    private fun setSubCategory() {
        viewModelScope.launch { subCategoryList.postValue(watchRepository.getSubcategory()) }
    }

    private fun setBrand() {
        viewModelScope.launch { brandList.postValue(watchRepository.getBrand()) }
    }

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

    fun setTopRatedWatches(count: Int) {
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

    private fun setSearchResult(list: List<ProductWithBrandAndImages>) {
        watchList.value = list
        _searchStatus.value = SEARCH_COMPLETED
    }

    fun getSearchResult(): LiveData<List<ProductWithBrandAndImages>> {
        return watchList
    }

    fun setProductsWithBrandAndImagesByQuery(searchQuery: String) = viewModelScope.launch {
        productListTitle = searchQuery
        _searchStatus.value = SEARCH_INITIATED
        val searchList = getQueryAsList(searchQuery)
        watchRepository.getProductWithBrandAndImagesByQuery(searchList).also {
            setSearchResult(it)
        }
    }

    private fun getQueryAsList(query: String): List<String> {
        val list = query.split(" ", ",", ", ", " ,","'")
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
        ),
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
            watchList.postValue(filterResult)
        }

    }

    companion object {
        const val SEARCH_COMPLETED = 1
        const val SEARCH_INITIATED = 0
        const val SEARCH_NOT_INITIATED = -1
    }

}