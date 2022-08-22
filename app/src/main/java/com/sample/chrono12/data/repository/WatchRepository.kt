package com.sample.chrono12.data.repository

import com.sample.chrono12.data.dao.WatchDao
import com.sample.chrono12.data.entities.Category
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.SubCategory
import com.sample.chrono12.data.entities.relations.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WatchRepository(private val watchDao: WatchDao) {

    suspend fun getProducts(): List<Product> = withContext(Dispatchers.IO) {
        watchDao.getProducts()
    }

    suspend fun getProduct(id: Int): Product = withContext(Dispatchers.IO) {
        watchDao.getProduct(id)
    }

    suspend fun getCategory(): List<Category> = withContext(Dispatchers.IO) {
        watchDao.getCategory()
    }

    suspend fun getSubcategory(): List<SubCategory> = withContext(Dispatchers.IO) {
        watchDao.getSubCategory()
    }

    suspend fun getCategoryWithSubCategory(): List<CategoryWithSubCategory> =
        withContext(Dispatchers.IO) {
            watchDao.getCategoryWithSubCategory()
        }

    suspend fun getProductWithImages(): List<ProductWithImages> =
        withContext(Dispatchers.IO) {
            watchDao.getProductWithImages()
        }


    suspend fun getProductWithSubCategory(): List<ProductWithSubCategory> =
        withContext(Dispatchers.IO) {
            watchDao.getProductWithSubCategory()
        }

    suspend fun getSubCategoryWithProduct(): List<SubCategoryWithProduct> =
        withContext(Dispatchers.IO){
            watchDao.getSubCategoryWithProduct()
        }

    suspend fun getProductWithImages(subCategoryId: Int): List<SubCategoryWithProduct> =
        withContext(Dispatchers.IO){
            watchDao.getProductWithImages(subCategoryId)
        }

    suspend fun getCartWithProductAndImages(): List<CartWithProductAndImages> =
        withContext(Dispatchers.IO){
            watchDao.getCartWithProductAndImages()
        }

    suspend fun getProductOrderedWithProductAndImages(): List<ProductOrderedWithProductAndImages> =
        withContext(Dispatchers.IO){
            watchDao.getProductOrderedWithProductAndImages()
        }

    suspend fun getBrandWithProductAndImages(): List<BrandWithProductAndImages> =
        withContext(Dispatchers.IO){
            watchDao.getBrandWithProductAndImages()
        }
}