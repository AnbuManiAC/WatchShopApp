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

    @Transaction
    @Query("SELECT * FROM SubCategory")
    suspend fun getSubCategoryWithProduct(): SubCategoryWithProduct

    @Query("SELECT * FROM Product where brandId = :brandId")
    suspend fun getBrandWithProductAndImages(brandId: Int): List<ProductWithBrandAndImages>

    @Query("SELECT * FROM Product ORDER BY totalRating DESC LIMIT :count")
    suspend fun getTopRatedWatches(count: Int): List<ProductWithBrandAndImages>

    @Query("SELECT * FROM Product")
    suspend fun getAllWatches(): List<ProductWithBrandAndImages>

    @Transaction
    @RawQuery
    suspend fun getProductWithBrandAndImagesByQuery(searchQueries: SupportSQLiteQuery): List<ProductWithBrandAndImages>

    @Transaction
    @Query("Select * from Product inner join ProductBrand on Product.brandId = ProductBrand.brandId where productId in " +
            "(select productId from ProductCategoryCrossRef where subCategoryId in (:args1) And productId in " +
            "(select productId from ProductCategoryCrossRef where subCategoryId in (:args2)) And productId in" +
            "(select productId from ProductCategoryCrossRef where subCategoryId in (:args3))) "+
            "and brandName in (:args4)")
    suspend fun getFilterResult(
        args1: List<Int> = listOf(1,2,3),
        args2: List<Int> = listOf(4,5,6),
        args3: List<Int> = listOf(7,8,9,10,11),
        args4: List<String> = listOf("Fastrack", "Titan", "Sonata", "Timex", "Maxima", "Helix", "Fossil")
        ): List<ProductWithBrandAndImages>

}