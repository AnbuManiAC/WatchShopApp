package com.sample.chrono12.data.repository

import com.sample.chrono12.data.dao.UserDao
import com.sample.chrono12.data.entities.Cart
import com.sample.chrono12.data.entities.User
import com.sample.chrono12.data.entities.WishList
import com.sample.chrono12.data.entities.relations.CartWithProductInfo
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.data.entities.relations.WishListWithProductInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun createUser(user: User): Long = withContext(Dispatchers.IO){
        userDao.createUser(user)
    }

    suspend fun isExistingEmail(emailId: String): Int = withContext(Dispatchers.IO){
        userDao.isExistingEmail(emailId)
    }

    suspend fun validatePassword(emailId: String, password: String): Int = withContext(Dispatchers.IO){
        userDao.validatePassword(emailId, password)
    }

    suspend fun getUserId(emailId: String): Long = withContext(Dispatchers.IO){
        userDao.getUserId(emailId)
    }

    suspend fun getUser(userId: Long): User = withContext(Dispatchers.IO){
        userDao.getUser(userId)
    }

    suspend fun insertIntoWishList(wishList: WishList): Long = withContext(Dispatchers.IO){
        userDao.insertIntoWishList(wishList)
    }

    suspend fun deleteFromWishlist(productId: Int, userId: Int) = withContext(Dispatchers.IO){
        userDao.deleteFromWishlist(productId, userId)
    }

    suspend fun isProductInUserWishList(productId: Int, userId: Int): Int = withContext(Dispatchers.IO){
        userDao.isProductInUserWishList(productId, userId)
    }

    suspend fun getUserWishListItems(userId: Int): List<WishListWithProductInfo> =
        withContext(Dispatchers.IO){
            userDao.getUserWishListItems(userId)
        }

    suspend fun insertIntoCart(cart: Cart): Long = withContext(Dispatchers.IO){
        userDao.insertIntoCart(cart)
    }

    suspend fun deleteFromCart(productId: Int, userId: Int) = withContext(Dispatchers.IO){
        userDao.deleteFromCart(productId, userId)
    }

    suspend fun isProductInUserCart(productId: Int, userId: Int): Int =
        withContext(Dispatchers.IO){
            userDao.isProductInUserCart(productId, userId)
        }

    suspend fun getUserCartItems(userId: Int): List<CartWithProductInfo> =
        withContext(Dispatchers.IO){
            userDao.getUserCartItems(userId)
        }


}