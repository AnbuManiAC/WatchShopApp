package com.sample.chrono12.data.dao

import androidx.room.*
import com.sample.chrono12.data.entities.Cart
import com.sample.chrono12.data.entities.User
import com.sample.chrono12.data.entities.WishList
import com.sample.chrono12.data.entities.relations.CartWithProductInfo
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.data.entities.relations.WishListWithProductInfo

@Dao
interface UserDao {

    //Signup, Signin
    @Insert
    suspend fun createUser(user: User) : Long

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :emailId)")
    suspend fun isExistingEmail(emailId: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :emailId AND password = :password)")
    suspend fun validatePassword(emailId: String, password: String): Int

    @Query("SELECT userId FROM User WHERE email = :emailId")
    suspend fun getUserId(emailId: String): Long

    @Query("SELECT * FROM User WHERE userId = :userId")
    suspend fun getUser(userId: Long): User

    //User Wishlist
    @Insert
    suspend fun insertIntoWishList(wishList: WishList): Long

    @Query("DELETE FROM WishList WHERE productId = :productId AND userId = :userId")
    suspend fun deleteFromWishlist(productId: Int, userId: Int)

    @Query("SELECT EXISTs(SELECT 1 FROM Wishlist WHERE userId = :userId AND productId = :productId)")
    suspend fun isProductInUserWishList(productId: Int, userId: Int): Int

    @Transaction
    @Query("SELECT * FROM WishList WHERE userId = :userId")
    suspend fun getUserWishListItems(userId: Int): List<WishListWithProductInfo>

    //User Cart
    @Insert
    suspend fun insertIntoCart(cart: Cart): Long

    @Query("DELETE FROM Cart WHERE productId = :productId AND userId = :userId")
    suspend fun deleteFromCart(productId: Int, userId: Int)

    @Query("SELECT EXISTs(SELECT 1 FROM Cart WHERE userId = :userId AND productId = :productId)")
    suspend fun isProductInUserCart(productId: Int, userId: Int): Int

    @Transaction
    @Query("SELECT * FROM Cart WHERE userId = :userId")
    suspend fun getUserCartItems(userId: Int): List<CartWithProductInfo>

}