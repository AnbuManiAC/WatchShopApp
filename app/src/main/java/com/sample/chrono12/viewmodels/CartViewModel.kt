package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chrono12.data.entities.Cart
import com.sample.chrono12.data.entities.relations.CartWithProductInfo
import com.sample.chrono12.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    fun getCartItems(userId: Int): LiveData<List<CartWithProductInfo>> =
        userRepository.getUserCartItems(userId)

    fun addProductToUserCart(cartItem: Cart){
        viewModelScope.launch {
            userRepository.insertIntoCart(cartItem)
        }
    }

    suspend fun isProductInUserCart(productId: Int, userId: Int): Boolean =
        userRepository.isProductInUserCart(productId, userId)==1

    fun removeProductFromUserCart(productId: Int, userId: Int){
        viewModelScope.launch {
            userRepository.deleteFromCart(productId, userId)
        }
    }

    fun updateQuantity(productId: Int, userId: Int, quantity: Int){
        viewModelScope.launch {
            userRepository.updateQuantity(productId, userId, quantity)

        }
    }
}