package com.sample.chrono12.data.repository

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.sample.chrono12.data.dao.UserDao
import com.sample.chrono12.data.entities.*
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.data.entities.relations.CartWithProductInfo
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.data.entities.relations.WishListWithProductInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

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

    fun getUserWishListItems(userId: Int): LiveData<List<WishListWithProductInfo>> =
            userDao.getUserWishListItems(userId)

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

    fun getUserCartItems(userId: Int): LiveData<List<CartWithProductInfo>> =
            userDao.getUserCartItems(userId)

    suspend fun updateQuantity(productId: Int, userId: Int, quantity: Int) =
        userDao.updateQuantity(productId, userId, quantity)

    //Suggestion
    suspend fun getSuggestions(userId: Int): List<SearchSuggestion> = withContext(Dispatchers.IO){
        userDao.getSuggestions(userId)
    }

    suspend fun insertSuggestion(suggestion: SearchSuggestion) = withContext(Dispatchers.IO){
        userDao.insertSuggestion(suggestion)
    }

    suspend fun removeSuggestion(suggestion: SearchSuggestion) = withContext(Dispatchers.IO){
        userDao.removeSuggestion(suggestion)
    }

    suspend fun isSuggestionPresent(suggestion: String): Boolean = withContext(Dispatchers.IO){
        userDao.isSuggestionPresent(suggestion)==1
    }

    fun getUserAddresses(userId: Int): LiveData<AddressGroupWithAddress> =
        userDao.getUserAddresses(userId, )

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

    suspend fun getAddressById(addressId: Int): Address
        = userDao.getAddressById(addressId)


    fun getAddressGroupWithAddresses(userId: Int): LiveData<List<AddressGroupWithAddress>> =
        userDao.getAddressGroupWithAddresses(userId)

    fun getAddressGroupWithAddresses(userId: Int, addressGroupId: Int): LiveData<AddressGroupWithAddress> =
        userDao.getAddressGroupWithAddresses(userId, addressGroupId)

    fun getAddressGroupWithAddressByAddressId(userId: Int, addressGroupId: Int , addressId: Int): LiveData<AddressGroupWithAddress> =
        userDao.getAddressGroupWithAddressByAddressId(userId, addressGroupId, addressId)

    suspend fun getAddressGroupId(userId: Int, groupName: String) =
        userDao.getAddressGroupId(userId, groupName)


    suspend fun insertAddress(address: Address):Long = withContext(Dispatchers.IO){
        userDao.insertIntoAddress(address)
    }

    suspend fun insertAddressGroup(addressGroup: AddressGroup):Long = withContext(Dispatchers.IO){
        userDao.insertIntoAddressGroup(addressGroup)
    }

    suspend fun updateAddressGroupName(addressGroupId: Int, groupName: String) =
        withContext(Dispatchers.IO){
            userDao.updateAddressGroupName(addressGroupId, groupName)
        }

    suspend fun deleteAddressGroup(addressGroupId: Int) =
        withContext(Dispatchers.IO){
            userDao.deleteAddressGroup(addressGroupId)
        }

    suspend fun insertAddressAndGroupCrossRef(userId: Int, addressId: Int, addressGroupName: String) =
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

    suspend fun insertOrder(order: Order):Long =
        withContext(Dispatchers.IO){
            userDao.insertOrder(order)
        }

    suspend fun insertProductOrdered(productOrdered: ProductOrdered) =
        withContext(Dispatchers.IO){
            userDao.insertProductOrdered(productOrdered)
        }
}