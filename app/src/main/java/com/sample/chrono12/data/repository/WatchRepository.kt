package com.sample.chrono12.data.repository

import androidx.lifecycle.LiveData
import com.sample.chrono12.data.dao.WatchDao
import com.sample.chrono12.data.entities.*
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

    suspend fun getBrand(): List<ProductBrand> = withContext(Dispatchers.IO) {
        watchDao.getBrand()
    }

    suspend fun getCategoryWithSubCategory(): List<CategoryWithSubCategory> =
        withContext(Dispatchers.IO) {
            watchDao.getCategoryWithSubCategory()
        }

    suspend fun getProductWithImages(): List<ProductWithImages> =
        withContext(Dispatchers.IO) {
            watchDao.getProductWithImages()
        }

    suspend fun getProductWithImages(productId: Int): ProductWithImages =
        withContext(Dispatchers.IO) {
            watchDao.getProductWithImages(productId)
        }

    suspend fun getProductBrand(brandId: Int): ProductBrand =
        withContext(Dispatchers.IO){
            watchDao.getProductBrand(brandId)
        }

    suspend fun getProductWithBrandAndImages(productId: Int): ProductWithBrandAndImages =
        withContext(Dispatchers.IO){
            watchDao.getProductWithBrandAndImages(productId)
        }


    suspend fun getProductWithSubCategory(): List<ProductWithSubCategory> =
        withContext(Dispatchers.IO) {
            watchDao.getProductWithSubCategory()
        }

    suspend fun getProductDetail(productId: Int): List<ProductDetail> =
        withContext(Dispatchers.IO) {
            watchDao.getProductDetail(productId)
        }

    suspend fun getSubCategoryWithProduct(subCategoryId: Int): SubCategoryWithProduct =
        withContext(Dispatchers.IO){
            watchDao.getSubCategoryWithProduct(subCategoryId)
        }

    suspend fun getCategoryProductWithImages(subCategoryId: Int): List<SubCategoryWithProduct> =
        withContext(Dispatchers.IO){
            watchDao.getCategoryProductWithImages(subCategoryId)
        }

    suspend fun getCartWithProductAndImages(): List<CartWithProductAndImages> =
        withContext(Dispatchers.IO){
            watchDao.getCartWithProductAndImages()
        }

    suspend fun getProductOrderedWithProductAndImages(): List<ProductOrderedWithProductAndImages> =
        withContext(Dispatchers.IO){
            watchDao.getProductOrderedWithProductAndImages()
        }

    suspend fun getBrandWithProductAndImages(brandId: Int): List<ProductWithBrandAndImages> =
        withContext(Dispatchers.IO){
            watchDao.getBrandwithProductAndImages(brandId)
        }
}