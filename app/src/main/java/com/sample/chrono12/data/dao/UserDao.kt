package com.sample.chrono12.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
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


    //Addresss
    @Query("SELECT * FROM AddressGroup WHERE userId =:userId AND groupName =:def")
    fun getUserAddresses(userId: Int, def: String = "default"): LiveData<AddressGroupWithAddress>

//    @Query("SELECT * FROM Address, AddressGroup WHERE AddressGroup.userId =:userId AND AddressGroup.groupName =:def")
//    fun getAddressesWithException(userId: Int, addressIds: List<Int>, def: String = "default"): LiveData<AddressGroupWithAddress>

//    @Transaction
//    @Query("select * from AddressGroup inner join AddressAndGroupCrossRef ON AddressGroup.addressGroupId = AddressAndGroupCrossRef.addressGroupId " +
//            "inner join Address on Address.addressId = AddressAndGroupCrossRef.addressId where Address.addressId not in (:addressIds) and AddressGroup.groupName = :def " +
//            "and AddressGroup.userId = :userId")
//    fun getAddressesWithException(userId: Int, addressIds: List<Int>, def: String = "default"): LiveData<AddressGroupWithAddress>

//    @RawQuery
//    fun getAddressesWithException(query: SupportSQLiteQuery): LiveData<AddressGroupWithAddress>

    @Query("SELECT * FROM Address WHERE addressId = :addressId")
    suspend fun getAddressById(addressId: Int): Address

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntoAddress(address: Address):Long

    @Query("DELETE FROM Address WHERE addressId = :addressId")
    fun deleteAddress(addressId: Int)

    //Address group
    @Query("SELECT * FROM AddressGroup WHERE userId =:userId AND groupName!=:def")
    fun getAddressGroupWithAddresses(userId: Int, def: String = "default"): LiveData<List<AddressGroupWithAddress>>

    @Query("SELECT * FROM AddressGroup WHERE userId =:userId And addressGroupId = :addressGroupId")
    fun getAddressGroupWithAddresses(userId: Int, addressGroupId: Int): LiveData<AddressGroupWithAddress>

    @Query("SELECT addressGroupId FROM AddressGroup where userId =:userId AND groupName =:groupName")
    suspend fun getAddressGroupId(userId: Int, groupName: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIntoAddressGroup(addressGroup: AddressGroup):Long

    @Query("UPDATE AddressGroup SET groupName = :groupName WHERE addressGroupId = :addressGroupId")
    suspend fun updateAddressGroupName(addressGroupId: Int, groupName: String)

    @Query("DELETE FROM AddressGroup WHERE addressGroupId = :addressGroupId")
    fun deleteAddressGroup(addressGroupId: Int)

    //Address and group CrossRef
    @Query("INSERT OR IGNORE INTO AddressAndGroupCrossRef(addressId, addressGroupId) values(:addressId, (SELECT addressGroupId FROM AddressGroup WHERE userId =:userId AND groupName =:addressGroupName))")
    suspend fun insertIntoAddressAndGroupCrossRef(userId: Int, addressId: Int, addressGroupName: String)

    @Query("DELETE FROM AddressAndGroupCrossRef WHERE addressId = :addressId AND addressGroupId = :addressGroupId")
    suspend fun deleteAddressFromGroup(addressId: Int, addressGroupId: Int)


}