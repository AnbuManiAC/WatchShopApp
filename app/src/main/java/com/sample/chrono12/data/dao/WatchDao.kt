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

    @Query("SELECT * FROM Product WHERE productId = :id")
    suspend fun getProduct(id: Int): Product

    @Query("SELECT * FROM Product")
    suspend fun getProductWithImages(): List<ProductWithImages>

    @Query("SELECT * FROM ProductBrand WHERE brandId = :brandId")
    suspend fun getProductBrand(brandId: Int): ProductBrand

    @Transaction
    @Query("SELECT * FROM Product where productId = :productId")
    suspend fun getProductWithBrandAndImages(productId: Int): ProductWithBrandAndImages

    @Transaction
    @Query("SELECT * FROM Product where productId = :productId")
    suspend fun getProductWithImages(productId: Int): ProductWithImages

    @Query("SELECT imageUrl FROM ProductImage WHERE productId = :productId ORDER BY imageIndex")
    suspend fun getProductsImages(productId: Int): List<String>

    @Query("SELECT * FROM Category")
    suspend fun getCategory(): List<Category>

    @Query("SELECT * FROM SubCategory")
    suspend fun getSubCategory(): List<SubCategory>

    @Transaction
    @Query("SELECT * FROM Category")
    suspend fun getCategoryWithSubCategory(): List<CategoryWithSubCategory>

    @Query("SELECT * FROM ProductBrand")
    suspend fun getBrand(): List<ProductBrand>

    @Query("SELECT * FROM ProductDetail WHERE productId = :productId ORDER BY productDetailId")
    suspend fun getProductDetail(productId: Int): List<ProductDetail>

    @Query("SELECT * FROM Product")
    suspend fun getProductWithSubCategory(): List<ProductWithSubCategory>

    @Transaction
    @Query("SELECT * FROM SubCategory where subCategoryId = :subCategoryId")
    suspend fun getSubCategoryWithProduct(subCategoryId: Int): SubCategoryWithProduct

    @Query("SELECT * FROM SubCategory where subCategoryId = :subCategoryIds")
    suspend fun getCategoryProductWithImages(subCategoryIds: Int): List<SubCategoryWithProduct>

    @Query("SELECT * FROM Cart")
    suspend fun getCartWithProductAndImages(): List<CartWithProductAndImages>

    @Query("SELECT * FROM ProductOrdered")
    suspend fun getProductOrderedWithProductAndImages(): List<ProductOrderedWithProductAndImages>

    @Query("SELECT * FROM Product where brandId = :brandId")
    suspend fun getBrandwithProductAndImages(brandId: Int): List<ProductWithBrandAndImages>
}