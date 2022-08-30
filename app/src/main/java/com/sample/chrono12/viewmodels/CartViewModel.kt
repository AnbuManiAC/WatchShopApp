package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val totalOriginalPrice = MutableLiveData<Int>()
    private val totalCurrentPrice = MutableLiveData<Int>()

    fun getTotalOriginPrice(): LiveData<Int> = totalOriginalPrice
    fun getTotalCurrentPrice(): LiveData<Int> = totalCurrentPrice

    private fun setTotalOriginalPrice(price: Int){
        totalOriginalPrice.value = price
    }

    private fun setTotalCurrentPrice(price: Int){
        totalCurrentPrice.value = price
    }

    fun getCartItems(userId: Int): LiveData<List<CartWithProductInfo>> = userRepository.getUserCartItems(userId)


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

    fun initPriceCalculating(list: List<CartWithProductInfo>) {
        var originalPrice = 0
        var currentPrice = 0
        list.forEach {

            originalPrice += it.productWithBrandAndImagesList.productWithBrand.product.originalPrice.toInt()*it.cart.quantity
            currentPrice += it.productWithBrandAndImagesList.productWithBrand.product.currentPrice.toInt()*it.cart.quantity
        }
        setTotalCurrentPrice(currentPrice)
        setTotalOriginalPrice(originalPrice)
    }
}