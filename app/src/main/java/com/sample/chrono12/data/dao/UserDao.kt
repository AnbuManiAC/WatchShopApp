package com.sample.chrono12.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.sample.chrono12.data.entities.*
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.data.entities.relations.CartWithProductInfo
import com.sample.chrono12.data.entities.relations.OrderedProductInfo
import com.sample.chrono12.data.entities.relations.WishListWithProductInfo
import com.sample.chrono12.data.models.OrderInfo

@Dao
interface UserDao {

    //Signup, Sign in
    @Insert
    suspend fun createUser(user: User): Long

    @Query("UPDATE USER SET image = :imageName where userId = :userId")
    suspend fun addProfilePicture(imageName: String, userId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :emailId)")
    suspend fun isExistingEmail(emailId: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE mobileNumber = :mobile)")
    suspend fun isExistingMobile(mobile: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :emailId AND password = :password)")
    suspend fun validatePassword(emailId: String, password: String): Int

    @Query("SELECT userId FROM User WHERE email = :emailId")
    suspend fun getUserId(emailId: String): Long

    @Query("SELECT * FROM User WHERE userId = :userId")
    suspend fun getUser(userId: Long): User

    @Query("UPDATE User SET image = Null where userId = :userId")
    suspend fun deleteProfilePicture(userId: Int)

    //User Wishlist
    @Insert
    suspend fun insertIntoWishList(wishList: WishList): Long

    @Query("DELETE FROM WishList WHERE productId = :productId AND userId = :userId")
    suspend fun deleteFromWishlist(productId: Int, userId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM Wishlist WHERE userId = :userId AND productId = :productId)")
    suspend fun isProductInUserWishList(productId: Int, userId: Int): Int

    @Transaction
    @Query("SELECT * FROM WishList WHERE userId = :userId ORDER BY wishListId DESC")
    fun getUserWishListItems(userId: Int): LiveData<List<WishListWithProductInfo>>

    //User Cart
    @Insert
    suspend fun insertIntoCart(cart: Cart): Long

    @Query("DELETE FROM Cart WHERE productId = :productId AND userId = :userId")
    suspend fun deleteFromCart(productId: Int, userId: Int)

    @Query("SELECT EXISTs(SELECT 1 FROM Cart WHERE userId = :userId AND productId = :productId)")
    suspend fun isProductInUserCart(productId: Int, userId: Int): Int

    @Transaction
    @Query("SELECT * FROM Cart WHERE userId = :userId ORDER BY cartId DESC")
    fun getUserCartItems(userId: Int): LiveData<List<CartWithProductInfo>>

    @Query("UPDATE Cart SET quantity = :quantity where userId = :userId AND productId = :productId")
    suspend fun updateQuantity(productId: Int, userId: Int, quantity: Int)

    @Query("UPDATE Product set stockCount = :stockCount where productId = :productId")
    suspend fun updateStockCount(productId: Int, stockCount: Int)

    @Query("DELETE FROM Cart WHERE userId = :userId")
    suspend fun deleteCartItems(userId: Int)

    //Suggestions
    @Query("SELECT * FROM SearchSuggestion WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getSearchHistory(userId: Int): List<SearchSuggestion>

    @RawQuery
    suspend fun getSuggestions(suggestionQuery: SupportSQLiteQuery): List<SearchSuggestion>


    @Insert
    suspend fun insertSuggestion(suggestion: SearchSuggestion)

    @Delete
    suspend fun removeSuggestion(suggestion: SearchSuggestion)

    @Query("SELECT EXISTS(SELECT 1 FROM SearchSuggestion WHERE suggestion = :suggestion)")
    suspend fun isSuggestionPresent(suggestion: String): Int


    //Address
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
    suspend fun insertIntoAddress(address: Address): Long

    @Update
    suspend fun editAddress(address: Address)

    @Query("DELETE FROM Address WHERE addressId = :addressId")
    fun deleteAddress(addressId: Int)

    //Address group
    @Query("SELECT * FROM AddressGroup WHERE userId =:userId AND groupName!=:def ORDER BY addressGroupId DESC")
    fun getAddressGroupWithAddresses(
        userId: Int,
        def: String = "default"
    ): LiveData<List<AddressGroupWithAddress>>

    @Query("SELECT * FROM AddressGroup WHERE userId =:userId And addressGroupId = :addressGroupId")
    fun getAddressGroupWithAddresses(
        userId: Int,
        addressGroupId: Int
    ): LiveData<AddressGroupWithAddress>

    @Query("SELECT * FROM AddressGroup, Address WHERE userId =:userId And addressId = :addressId And groupName = :groupName")
    fun getAddressGroupWithAddressByAddressId(
        userId: Int,
        addressId: Int,
        groupName: String
    ): LiveData<AddressGroupWithAddress>

    @Query("SELECT addressGroupId FROM AddressGroup where userId =:userId AND groupName =:groupName")
    suspend fun getAddressGroupId(userId: Int, groupName: String): Int

    @Query("SELECT groupName FROM AddressGroup where userId =:userId AND addressGroupId = :addressGroupId")
    fun getAddressGroupName(userId: Int, addressGroupId: Int): LiveData<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIntoAddressGroup(addressGroup: AddressGroup): Long

    @Query("SELECT EXISTS(SELECT 1 FROM AddressGroup WHERE userId = :userId and groupName = :groupName)")
    suspend fun isExistingAddressGroup(groupName: String, userId: Int): Int

    @Query("UPDATE AddressGroup SET groupName = :groupName WHERE addressGroupId = :addressGroupId")
    suspend fun updateAddressGroupName(addressGroupId: Int, groupName: String)

    @Query("DELETE FROM AddressGroup WHERE addressGroupId = :addressGroupId")
    fun deleteAddressGroup(addressGroupId: Int)

    //Address and group CrossRef
    @Query("INSERT OR IGNORE INTO AddressAndGroupCrossRef(addressId, addressGroupId) values(:addressId, (SELECT addressGroupId FROM AddressGroup WHERE userId =:userId AND groupName =:addressGroupName))")
    suspend fun insertIntoAddressAndGroupCrossRef(
        userId: Int,
        addressId: Int,
        addressGroupName: String
    )

    @Query("DELETE FROM AddressAndGroupCrossRef WHERE addressId = :addressId AND addressGroupId = :addressGroupId")
    suspend fun deleteAddressFromGroup(addressId: Int, addressGroupId: Int)

    //Order
    @Insert
    suspend fun insertOrder(order: Order): Long

    @Insert
    suspend fun insertProductOrdered(productOrdered: ProductOrdered): Long

    @Query("select distinct bulkOrderId,orderId,timestamp, sum(actualTotal) as totalPrice, sum(totalPrice) as currentPrice,count() as orderCount,(select count() from ProductOrdered where ProductOrdered.orderId = `Order`.orderId) as productCount from `Order` where userId = :userId group by bulkOrderId order by timestamp desc")
    suspend fun getOrderHistory(userId: Int): List<OrderInfo>

    @Query("select distinct bulkOrderId,orderId,timestamp, sum(actualTotal) as totalPrice, sum(totalPrice) as currentPrice,count() as orderCount,(select count() from ProductOrdered where ProductOrdered.orderId = `Order`.orderId) as productCount from `Order` where userId = :userId and bulkOrderId = :bulkOrderId group by bulkOrderId")
    suspend fun getOrderInfo(bulkOrderId: Int, userId: Int): OrderInfo

//    @Query("Select * FROM `Order` where bulkOrderId = :bulkOrderId and userId = :userId ORDER BY orderId ASC")
//    suspend fun getOrderDetail(bulkOrderId: Int, userId: Int): List<Order>

    @Query("Select * FROM `Order` where bulkOrderId = :bulkOrderId and userId = :userId ORDER BY orderId ASC")
    fun getOrderDetail(bulkOrderId: Int, userId: Int): LiveData<List<Order>>

    @Query("SELECT * FROM ProductOrdered where orderId = :orderId ORDER BY productOrderedId ASC")
    suspend fun getOrderedProductInfo(orderId: Int): List<OrderedProductInfo>

    @Query("DELETE FROM SearchSuggestion WHERE userId = :userId")
    suspend fun deleteSearchHistory(userId: Int)

    @Query("UPDATE `Order` set orderStatus = :orderStatus where orderId = :orderId")
    fun changeOrderStatus(orderId: Int, orderStatus: String)

}