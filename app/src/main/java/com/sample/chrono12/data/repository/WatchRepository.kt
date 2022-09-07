package com.sample.chrono12.data.repository

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
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

    suspend fun getSubCategoryWithProduct(): SubCategoryWithProduct =
        withContext(Dispatchers.IO){
            watchDao.getSubCategoryWithProduct()
        }

    suspend fun getBrandWithProductAndImages(brandId: Int): List<ProductWithBrandAndImages> =
        withContext(Dispatchers.IO){
            watchDao.getBrandWithProductAndImages(brandId)
        }

    suspend fun getTopRatedWatches(count: Int): List<ProductWithBrandAndImages> =
        withContext(Dispatchers.IO){
            watchDao.getTopRatedWatches(count)
        }

    suspend fun getAllWatches(): List<ProductWithBrandAndImages> =
        withContext(Dispatchers.IO){
            watchDao.getAllWatches()
        }

    suspend fun getProductWithBrandAndImagesByQuery(searchQuery: List<String>): List<ProductWithBrandAndImages> = withContext(Dispatchers.IO){
        val products:List<ProductWithBrandAndImages> = watchDao.getProductWithBrandAndImagesByQuery(generateSearchQuery(searchQuery))
        products.forEach {
            Log.d("SEARCH", "${it.productWithBrand.product.productId}")
        }
        Log.d("SEARCH", "Product count ${products.size}")
        return@withContext products
    }

    private fun generateSearchQuery(query: List<String>): SupportSQLiteQuery {
        var queryText = "SELECT * FROM PRODUCT WHERE productId IN (SELECT productId FROM Product WHERE (name LIKE \'${query[0]}\')"
        val args = arrayListOf<String>()
        query.drop(1).forEach { searchStringPart ->
            args.add(searchStringPart)
            queryText += " AND (name LIKE ?)"
        }
        queryText+=")"
        Log.d("Search","args - $args")
        Log.d("Search","args - $queryText")
        val simpleSQLiteQuery = SimpleSQLiteQuery(queryText,args.toArray())
        Log.i("Search","query as SimpleSQLiteQuery - ${simpleSQLiteQuery.sql}")
        return simpleSQLiteQuery
    }

    suspend fun getFilterResult(args1:List<Int>, args2:List<Int>, args3:List<Int>, args4:List<String>): List<ProductWithBrandAndImages> =
        withContext(Dispatchers.IO){
            watchDao.getFilterResult(args1, args2, args3, args4)
        }
}