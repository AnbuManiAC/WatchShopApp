package com.sample.chrono12.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import com.sample.chrono12.data.entities.*
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.data.entities.relations.ProductWithImages
import com.sample.chrono12.data.entities.relations.SubCategoryWithProduct

@Dao
interface WatchDao {

    @Query("SELECT * FROM Product WHERE productId = :id")
    suspend fun getProduct(id: Int): Product

    @Query("SELECT * FROM ProductBrand WHERE brandId = :brandId")
    suspend fun getProductBrand(brandId: Int): ProductBrand

    @Transaction
    @Query("SELECT * FROM Product where productId = :productId")
    suspend fun getProductWithBrandAndImages(productId: Int): ProductWithBrandAndImages

    @Transaction
    @Query("SELECT * FROM Product where productId = :productId")
    suspend fun getProductWithImages(productId: Int): ProductWithImages

    @Query("SELECT * FROM Category")
    suspend fun getCategory(): List<Category>

    @Query("SELECT * FROM SubCategory")
    suspend fun getSubCategory(): List<SubCategory>

    @Query("SELECT * FROM ProductBrand")
    suspend fun getBrand(): List<ProductBrand>

    @Query("SELECT * FROM ProductDetail WHERE productId = :productId ORDER BY productDetailId")
    suspend fun getProductDetail(productId: Int): List<ProductDetail>

    @Transaction
    @Query("SELECT * FROM SubCategory where subCategoryId = :subCategoryId")
    suspend fun getSubCategoryWithProduct(subCategoryId: Int): SubCategoryWithProduct

    @Query("SELECT * FROM Product where brandId = :brandId")
    suspend fun getBrandWithProductAndImages(brandId: Int): List<ProductWithBrandAndImages>

    @Query("SELECT * FROM Product ORDER BY totalRating DESC LIMIT :count")
    suspend fun getTopRatedWatches(count: Int): List<ProductWithBrandAndImages>

    @Query("SELECT * FROM Product")
    suspend fun getAllWatches(): List<ProductWithBrandAndImages>

    @RawQuery
    suspend fun getProductWithBrandAndImagesByQuery(searchQueries: SupportSQLiteQuery): List<ProductWithBrandAndImages>

}