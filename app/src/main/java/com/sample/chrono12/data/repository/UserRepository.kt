package com.sample.chrono12.data.repository

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.sample.chrono12.data.dao.UserDao
import com.sample.chrono12.data.entities.*
import com.sample.chrono12.data.entities.relations.*
import com.sample.chrono12.data.models.OrderInfo
import com.sample.chrono12.data.models.OrderStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao) {

    suspend fun createUser(user: User): Long = withContext(Dispatchers.IO) {
        userDao.createUser(user)
    }

    suspend fun addProfilePicture(imagePath: String, userId: Int) = withContext(Dispatchers.IO){
        userDao.addProfilePicture(imagePath, userId)
    }

    suspend fun isExistingEmail(emailId: String): Int = withContext(Dispatchers.IO) {
        userDao.isExistingEmail(emailId)
    }

    suspend fun isExistingMobile(mobile: String): Int = withContext(Dispatchers.IO) {
        userDao.isExistingMobile(mobile)
    }

    suspend fun validatePassword(emailId: String, password: String): Int =
        withContext(Dispatchers.IO) {
            userDao.validatePassword(emailId, password)
        }

    suspend fun getUserId(emailId: String): Long = withContext(Dispatchers.IO) {
        userDao.getUserId(emailId)
    }

    suspend fun getUser(userId: Long): User = withContext(Dispatchers.IO) {
        userDao.getUser(userId)
    }

    suspend fun deleteProfilePicture(userId: Int) = withContext(Dispatchers.IO) {
        userDao.deleteProfilePicture(userId)
    }

    suspend fun insertIntoWishList(wishList: WishList): Long = withContext(Dispatchers.IO) {
        userDao.insertIntoWishList(wishList)
    }

    suspend fun deleteFromWishlist(productId: Int, userId: Int) = withContext(Dispatchers.IO) {
        userDao.deleteFromWishlist(productId, userId)
    }

    suspend fun isProductInUserWishList(productId: Int, userId: Int): Int =
        withContext(Dispatchers.IO) {
            userDao.isProductInUserWishList(productId, userId)
        }

    fun getUserWishListItems(userId: Int): LiveData<List<WishListWithProductInfo>> =
        userDao.getUserWishListItems(userId)

    suspend fun insertIntoCart(cart: Cart): Long = withContext(Dispatchers.IO) {
        userDao.insertIntoCart(cart)
    }

    suspend fun deleteFromCart(productId: Int, userId: Int) = withContext(Dispatchers.IO) {
        userDao.deleteFromCart(productId, userId)
    }

    suspend fun isProductInUserCart(productId: Int, userId: Int): Int =
        withContext(Dispatchers.IO) {
            userDao.isProductInUserCart(productId, userId)
        }

    fun getUserCartItems(userId: Int): LiveData<List<CartWithProductInfo>> =
        userDao.getUserCartItems(userId)

    suspend fun updateQuantity(productId: Int, userId: Int, quantity: Int) =
        userDao.updateQuantity(productId, userId, quantity)

    suspend fun updateStockCount(productId: Int, stockCount: Int) = withContext(Dispatchers.IO){
        userDao.updateStockCount(productId, stockCount)
    }

    //Suggestion
    suspend fun getSearchHistory(userId: Int): List<SearchSuggestion> = withContext(Dispatchers.IO){
        userDao.getSearchHistory(userId)
    }

    suspend fun getSearchSuggestion(searchQuery: List<String>, userId: Int): List<SearchSuggestion> = withContext(Dispatchers.IO){
        userDao.getSuggestions(generateSuggestionQuery(searchQuery, userId))
    }

    private fun generateSuggestionQuery(query: List<String>, userId: Int): SupportSQLiteQuery {
        var queryText = "SELECT * FROM SearchSuggestion WHERE (suggestion LIKE \'${query[0]}\'"
        val args = arrayListOf<String>()
        query.drop(1).forEach { searchStringPart ->
            args.add(searchStringPart)
            queryText += " OR suggestion LIKE ?"
        }
        queryText += ") AND (userId is Null OR userId = $userId) ORDER BY timestamp DESC"
        return SimpleSQLiteQuery(queryText, args.toArray())
    }

    suspend fun insertSuggestion(suggestion: SearchSuggestion) = withContext(Dispatchers.IO) {
        userDao.insertSuggestion(suggestion)
    }

    suspend fun removeSuggestion(suggestion: SearchSuggestion) = withContext(Dispatchers.IO) {
        userDao.removeSuggestion(suggestion)
    }

    suspend fun isSuggestionPresent(suggestion: String): Boolean = withContext(Dispatchers.IO) {
        userDao.isSuggestionPresent(suggestion) == 1
    }

    fun getUserAddresses(userId: Int): LiveData<AddressGroupWithAddress> =
        userDao.getUserAddresses(userId)

//    fun getUserAddressesWithException(userId: Int, addressIds: List<Int>) =
//        userDao.getAddressesWithException(userId, addressIds)

//    fun getUserAddressesWithException(userId: Int, addressIds: List<Int>):LiveData<AddressGroupWithAddress> {
//        val def = "default"
//        val args = arrayListOf<String>("default")
//        val query = "select * from AddressGroup inner join AddressAndGroupCrossRef ON AddressGroup.addressGroupId = AddressAndGroupCrossRef.addressGroupId " +
//            "inner join Address on Address.addressId = AddressAndGroupCrossRef.addressId where Address.addressId != 64 and AddressGroup.groupName = \"default\" " +
//            "and AddressGroup.userId = 3"
//        return userDao.getAddressesWithException(SimpleSQLiteQuery(query))
//    }

    suspend fun getAddressById(addressId: Int): Address = userDao.getAddressById(addressId)


    fun getAddressGroupWithAddresses(userId: Int): LiveData<List<AddressGroupWithAddress>> =
        userDao.getAddressGroupWithAddresses(userId)

    fun getAddressGroupWithAddresses(
        userId: Int,
        addressGroupId: Int
    ): LiveData<AddressGroupWithAddress> =
        userDao.getAddressGroupWithAddresses(userId, addressGroupId)

    fun getAddressGroupWithAddressByAddressId(
        userId: Int,
        addressId: Int,
        groupName: String
    ): LiveData<AddressGroupWithAddress> =
        userDao.getAddressGroupWithAddressByAddressId(userId, addressId, groupName)

    suspend fun getAddressGroupId(userId: Int, groupName: String) =
        userDao.getAddressGroupId(userId, groupName)

    fun getAddressGroupName(userId: Int, addressGroupId: Int): LiveData<String> =
        userDao.getAddressGroupName(userId, addressGroupId)


    suspend fun insertAddress(address: Address): Long = withContext(Dispatchers.IO) {
        userDao.insertIntoAddress(address)
    }

    suspend fun editAddress(address: Address) = withContext(Dispatchers.IO) {
        userDao.editAddress(address)
    }

    suspend fun insertAddressGroup(addressGroup: AddressGroup): Long = withContext(Dispatchers.IO) {
        userDao.insertIntoAddressGroup(addressGroup)
    }

    suspend fun isExistingAddressGroup(groupName: String, userId: Int): Int = withContext(Dispatchers.IO) {
        userDao.isExistingAddressGroup(groupName, userId)
    }

    suspend fun updateAddressGroupName(addressGroupId: Int, groupName: String) =
        withContext(Dispatchers.IO) {
            userDao.updateAddressGroupName(addressGroupId, groupName)
        }

    suspend fun deleteAddressGroup(addressGroupId: Int) =
        withContext(Dispatchers.IO) {
            userDao.deleteAddressGroup(addressGroupId)
        }

    suspend fun insertAddressAndGroupCrossRef(
        userId: Int,
        addressId: Int,
        addressGroupName: String
    ) =
        withContext(Dispatchers.IO) {
            userDao.insertIntoAddressAndGroupCrossRef(userId, addressId, addressGroupName)
        }

    suspend fun deleteAddressFromGroup(addressId: Int, addressGroupId: Int) =
        withContext(Dispatchers.IO) {
            userDao.deleteAddressFromGroup(addressId, addressGroupId)
        }

    suspend fun deleteAddress(addressId: Int) =
        withContext(Dispatchers.IO) {
            userDao.deleteAddress(addressId)
        }

    suspend fun insertOrder(order: Order): Long =
        withContext(Dispatchers.IO) {
            userDao.insertOrder(order)
        }

    suspend fun insertProductOrdered(productOrdered: ProductOrdered) =
        withContext(Dispatchers.IO) {
            userDao.insertProductOrdered(productOrdered)
        }

    suspend fun getOrderHistory(userId: Int): List<OrderInfo> =
        withContext(Dispatchers.IO) {
            userDao.getOrderHistory(userId)
        }

    suspend fun getOrderInfo(bulkOrderId: Int, userId: Int): OrderInfo =
        withContext(Dispatchers.IO) {
            userDao.getOrderInfo(bulkOrderId, userId)
        }

    suspend fun deleteCartItems(userId: Int) =
        withContext(Dispatchers.IO) {
            userDao.deleteCartItems(userId)
        }

//    suspend fun getOrderDetail(bulkOrderId: Int, userId: Int): List<Order> =
//        withContext(Dispatchers.IO) {
//            userDao.getOrderDetail(bulkOrderId, userId)
//        }

    fun getOrderDetail(bulkOrderId: Int, userId: Int): LiveData<List<Order>> =
            userDao.getOrderDetail(bulkOrderId, userId)

    suspend fun getOrderedProductInfo(orderId: Int): List<OrderedProductInfo> =
        withContext(Dispatchers.IO) {
            userDao.getOrderedProductInfo(orderId)
        }

    suspend fun deleteSearchHistory(userId: Int) =
        withContext(Dispatchers.IO) {
            userDao.deleteSearchHistory(userId)
        }

    fun changeOrderStatus(orderId: Int, orderStatus: String) =
        userDao.changeOrderStatus(orderId, orderStatus)

}