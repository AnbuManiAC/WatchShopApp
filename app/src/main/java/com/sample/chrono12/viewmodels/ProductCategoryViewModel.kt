package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import com.sample.chrono12.data.entities.Category
import com.sample.chrono12.data.entities.SubCategory
import com.sample.chrono12.data.entities.relations.ProductWithImages
import com.sample.chrono12.data.entities.relations.SubCategoryWithProduct
import com.sample.chrono12.data.repository.WatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductCategoryViewModel @Inject constructor(
    private val watchRepository: WatchRepository
): ViewModel() {

    private val productWithImagesList = MutableLiveData<List<SubCategoryWithProduct>>()
    private val categoryList = MutableLiveData<List<Category>>()
    private val subCategoryList = MutableLiveData<List<SubCategory>>()

    init {
        setCategory()
        setSubCategory()
    }



    fun setCategory() {
        viewModelScope.launch { categoryList.postValue(watchRepository.getCategory()) }
    }

    fun setSubCategory() {
        viewModelScope.launch { subCategoryList.postValue(watchRepository.getSubcategory()) }
    }

    fun getCategory(): LiveData<List<Category>> = categoryList

    fun getSubCategory(): LiveData<List<SubCategory>> = subCategoryList

    fun setProductWithImageList(subCategoryId: Int){
        viewModelScope.launch { productWithImagesList.postValue(watchRepository.getProductWithImages(subCategoryId)) }
    }

    fun getProductWithImageList(): LiveData<List<SubCategoryWithProduct>> = productWithImagesList


}