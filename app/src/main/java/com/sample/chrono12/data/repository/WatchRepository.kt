package com.sample.chrono12.data.repository

import androidx.lifecycle.LiveData
import com.sample.chrono12.data.dao.WatchDao
import com.sample.chrono12.data.entities.*
import com.sample.chrono12.data.entities.relations.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WatchRepository(private val watchDao: WatchDao) {

    suspend fun getCategory(): List<Category> = withContext(Dispatchers.IO) {
        watchDao.getCategory()
    }

    suspend fun getSubcategory(): List<SubCategory> = withContext(Dispatchers.IO) {
        watchDao.getSubCategory()
    }

    suspend fun getBrand(): List<ProductBrand> = withContext(Dispatchers.IO) {
        watchDao.getBrand()
    }

    suspend fun getProductWithImages(productId: Int): ProductWithImages =
        withContext(Dispatchers.IO) {
            watchDao.getProductWithImages(productId)
        }

    suspend fun getProductWithBrandAndImages(productId: Int): ProductWithBrandAndImages =
        withContext(Dispatchers.IO){
            watchDao.getProductWithBrandAndImages(productId)
        }


    suspend fun getProductDetail(productId: Int): List<ProductDetail> =
        withContext(Dispatchers.IO) {
            watchDao.getProductDetail(productId)
        }

    suspend fun getSubCategoryWithProduct(subCategoryId: Int): SubCategoryWithProduct =
        withContext(Dispatchers.IO){
            watchDao.getSubCategoryWithProduct(subCategoryId)
        }

    suspend fun getBrandWithProductAndImages(brandId: Int): List<ProductWithBrandAndImages> =
        withContext(Dispatchers.IO){
            watchDao.getBrandwithProductAndImages(brandId)
        }

    suspend fun getTopRatedWatches(count: Int): List<ProductWithBrandAndImages> =
        withContext(Dispatchers.IO){
            watchDao.getTopRatedWatches(count)
        }

    suspend fun getAllWatches(): List<ProductWithBrandAndImages> =
        withContext(Dispatchers.IO){
            watchDao.getAllWatches()
        }
}