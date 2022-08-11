package com.sample.chrono12.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sample.chrono12.data.entities.*
import com.sample.chrono12.data.entities.relations.*

@Dao
interface WatchDao {

    @Query("SELECT * FROM Product")
    suspend fun getProducts(): List<Product>

    @Query("SELECT * FROM Product WHERE id = :id")
    suspend fun getProduct(id: Int): Product

    @Query("SELECT * FROM Product")
    suspend fun getProductWithImages(): List<ProductWithImages>

    @Query("SELECT imageUrl FROM ProductImage WHERE productId = :productId ORDER BY imageIndex")
    suspend fun getProductsImages(productId: Int): List<String>

    @Query("SELECT * FROM Category")
    suspend fun getCategory(): List<Category>

    @Transaction
    @Query("SELECT * FROM Category")
    suspend fun getCategoryWithSubCategory(): List<CategoryWithSubCategory>

    @Query("SELECT * FROM ProductBrand")
    suspend fun getBrand(): List<ProductBrand>

    @Query("SELECT * FROM ProductDetail WHERE productId = :productId ORDER BY id")
    suspend fun getProductDetail(productId: Int): ProductDetail

    @Query("SELECT * FROM Product")
    suspend fun getProductWithSubCategory(): List<ProductWithSubCategory>

    @Query("SELECT * FROM SubCategory")
    suspend fun getSubCategoryWithProduct(): List<SubCategoryWithProduct>

    @Query("SELECT * FROM Cart")
    suspend fun getCartWithProductAndImages(): List<CartWithProductAndImages>

    @Query("SELECT * FROM ProductOrdered")
    suspend fun getProductOrderedWithProductAndImages(): List<ProductOrderedWithProductAndImages>

    @Query("SELECT * FROM ProductBrand")
    suspend fun getBrandWithProductAndImages(): List<BrandWithProductAndImages>
}