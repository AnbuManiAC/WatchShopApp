package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chrono12.data.entities.WishList
import com.sample.chrono12.data.entities.relations.WishListWithProductInfo
import com.sample.chrono12.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val isProductInUserCart = MutableLiveData<Boolean>()

    fun getIsProductInUserCart(): LiveData<Boolean> = isProductInUserCart

    fun setIsProductInUserCart(isInCart: Boolean){
        isProductInUserCart.value = isInCart
    }

    fun getWishListItems(userId: Int): LiveData<List<WishListWithProductInfo>> = userRepository.getUserWishListItems(userId)

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
