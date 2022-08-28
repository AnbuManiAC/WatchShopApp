package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chrono12.data.entities.WishList
import com.sample.chrono12.data.entities.relations.CartWithProductAndImages
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.data.entities.relations.WishListWithProductInfo
import com.sample.chrono12.data.repository.UserRepository
import com.sample.chrono12.data.repository.WatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val wishListItems = MutableLiveData<List<WishListWithProductInfo>>()

    fun setWishListItems(userId: Int) {
        viewModelScope.launch {
            wishListItems.postValue(userRepository.getUserWishListItems(userId))
        }
    }

    fun getWishListItems(): LiveData<List<WishListWithProductInfo>> = wishListItems

    suspend fun isProductInUserWishList(productId: Int, userId: Int): Boolean = userRepository.isProductInUserWishList(productId,userId)==1

    fun addProductToUserWishList(wishListItem: WishList) {
        viewModelScope.launch {
            userRepository.insertIntoWishList(wishListItem)
        }
    }

    fun removeProductFromUserWishList(productId: Int, userId: Int) {
        viewModelScope.launch {
            userRepository.deleteFromWishlist(productId, userId)
        }
    }
}
