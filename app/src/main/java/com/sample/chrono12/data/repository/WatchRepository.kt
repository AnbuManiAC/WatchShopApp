package com.sample.chrono12.data.repository

import com.sample.chrono12.data.dao.WatchDao
import com.sample.chrono12.data.entities.Category
import com.sample.chrono12.data.entities.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WatchRepository(private val dao: WatchDao) {

    suspend fun getProducts(): List<Product> = withContext(Dispatchers.IO){
        dao.getProducts()
    }

    suspend fun getProduct(id: Int): Product = withContext(Dispatchers.IO){
        dao.getProduct(id)
    }

    suspend fun getCategory(): List<Category> = withContext(Dispatchers.IO){
        dao.getCategory()
    }
}