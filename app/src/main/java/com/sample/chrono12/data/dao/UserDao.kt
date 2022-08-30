package com.sample.chrono12.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sample.chrono12.data.entities.*
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.data.entities.relations.CartWithProductInfo
import com.sample.chrono12.data.entities.relations.WishListWithProductInfo
import com.sample.chrono12.ui.adapter.AddressAdapter

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

    @Query("SELECT EXISTS(SELECT 1 FROM Wishlist WHERE userId = :userId AND productId = :productId)")
    suspend fun isProductInUserWishList(productId: Int, userId: Int): Int

    @Transaction
    @Query("SELECT * FROM WishList WHERE userId = :userId")
    fun getUserWishListItems(userId: Int): LiveData<List<WishListWithProductInfo>>

    //User Cart
    @Insert
    suspend fun insertIntoCart(cart: Cart): Long

    @Query("DELETE FROM Cart WHERE productId = :productId AND userId = :userId")
    suspend fun deleteFromCart(productId: Int, userId: Int)

    @Query("SELECT EXISTs(SELECT 1 FROM Cart WHERE userId = :userId AND productId = :productId)")
    suspend fun isProductInUserCart(productId: Int, userId: Int): Int

    @Transaction
    @Query("SELECT * FROM Cart WHERE userId = :userId")
    fun getUserCartItems(userId: Int): LiveData<List<CartWithProductInfo>>

    @Query("UPDATE Cart SET quantity = :quantity where userId = :userId AND productId = :productId")
    suspend fun updateQuantity(productId: Int, userId: Int, quantity: Int)

    //Suggestions
    @Query("SELECT * FROM SearchSuggestion WHERE userId = 0 OR userId =:userId ORDER BY userId DESC")
    suspend fun getSuggestions(userId: Int): List<SearchSuggestion>

    @Insert
    suspend fun insertSuggestion(suggestion: SearchSuggestion)

    @Delete
    suspend fun removeSuggestion(suggestion: SearchSuggestion)

    @Query("SELECT EXISTS(SELECT 1 FROM SearchSuggestion WHERE suggestion = :suggestion)")
    suspend fun isSuggestionPresent(suggestion: String): Int

    @Query("SELECT * FROM AddressGroup WHERE userId =:userId AND groupName =:def")
    fun getUserAddresses(userId: Int, def: String = "default"): LiveData<AddressGroupWithAddress>

    @Query("SELECT * FROM AddressGroup WHERE userId =:userId")
    fun getAddressGroupWithAddresses(userId: Int): LiveData<List<AddressGroupWithAddress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntoAddress(address: Address)

    @Insert
    suspend fun insertIntoAddressGroup(addressGroup: AddressGroup)

    @Insert
    suspend fun insertIntoAddressAndGroupCrossRef(addressAndGroupCrossRef: AddressAndGroupCrossRef)

}