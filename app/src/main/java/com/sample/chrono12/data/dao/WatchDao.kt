package com.sample.chrono12.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.sample.chrono12.data.entities.Category
import com.sample.chrono12.data.entities.Product

@Dao
interface WatchDao {

    @Query("SELECT * FROM Product")
    suspend fun getProducts(): List<Product>

    @Query("SELECT * FROM Product WHERE productId = :id")
    suspend fun getProduct(id: Int): Product

    @Query("SELECT * FROM CATEGORY")
    suspend fun getCategory(): List<Category>

}