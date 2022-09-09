package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chrono12.data.entities.*
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.data.models.Response
import com.sample.chrono12.data.models.UserDetails
import com.sample.chrono12.data.models.UserField
import com.sample.chrono12.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private var isUserLoggedIn: Boolean = false
    private var loggedInUser: Long = 0
    private var userDetails = MutableLiveData<UserDetails>()
    private var userField = MutableLiveData<UserField>()
    private val suggestions = MutableLiveData<List<SearchSuggestion>>()
    private val address = MutableLiveData<Address>()
    private var addressId = MutableLiveData<Int>()
    private var addressGroupId = MutableLiveData<Int>()
    private val addressIds = MutableLiveData<List<Int>>()


    fun clearAddressIds() {
        null.also { addressIds.value = it }
    }

    fun setAddressIds(addressIdList: List<Int>) {
        addressIds.value = addressIdList
    }

    fun getAddressIds(): List<Int>? = addressIds.value

    fun clearUserFieldInfo() {
        null.also { userField.value = it }
    }

    fun clearAddressId() {
        null.also { addressId.value = it }
    }

    fun clearAddressGroupId() {
        null.also { addressGroupId.value = it }
    }

    fun getIsUserLoggedIn(): Boolean = isUserLoggedIn

    fun getLoggedInUser(): Long = loggedInUser

    fun setLoggedInUser(userId: Long) {
        loggedInUser = userId
        isUserLoggedIn = true
        setUserDetails(userId)
    }

    private fun setUserDetails(userId: Long) {
        viewModelScope.launch {
            val user: User = userRepository.getUser(userId)
            userDetails.value = UserDetails(user.name, user.email, user.mobileNumber)
        }
    }

    fun getUserDetails(): LiveData<UserDetails> = userDetails

    fun authenticateUser(emailId: String, password: String) = viewModelScope.launch {
        if (isExistingEmail(emailId)) {
            if (validatePassword(emailId, password)) {
                val userId = userRepository.getUserId(emailId)
                setLoggedInUser(userId)
                userField.value = UserField.ALL.also {
                    it.response = Response.SUCCESS.also { it.message = "Logged in successfully" }
                }
            } else {
                userField.value = UserField.PASSWORD.also {
                    it.response = Response.FAILURE.also { it.message = "Incorrect password" }
                }
            }
        } else {
            userField.value = UserField.EMAIL.also {
                it.response = Response.FAILURE.also { it.message = "Email does not exists" }
            }
        }
    }

    fun initiateSignUp(user: User) = viewModelScope.launch {
        if (isExistingEmail(user.email)) {
            userField.value = UserField.EMAIL.also {
                it.response = Response.FAILURE.also { it.message = "Email id already Exists" }
            }
            return@launch
        }
        createUser(user)
    }

    private fun createUser(user: User) = viewModelScope.launch {
        setLoggedInUser(userRepository.createUser(user))
        userField.value = UserField.ALL.also {
            it.response = Response.SUCCESS.also { it.message = "Sign up  successfull" }
        }
    }

    private suspend fun validatePassword(emailId: String, password: String): Boolean =
        userRepository.validatePassword(emailId, password) == 1

    private suspend fun isExistingEmail(emailId: String): Boolean =
        userRepository.isExistingEmail(emailId) == 1

    fun getUserFieldInfo(): LiveData<UserField> = userField

    fun logOutUser() {
        loggedInUser = 0
        isUserLoggedIn = false
    }

    fun setSuggestions() {
        viewModelScope.launch {
            suggestions.postValue(userRepository.getSuggestions(loggedInUser.toInt()))
        }
    }

    fun getSuggestions(): LiveData<List<SearchSuggestion>> = suggestions

    fun insertSuggestion(suggestion: String, timeStamp: Long) {
        viewModelScope.launch {
            if (isUserLoggedIn && !userRepository.isSuggestionPresent(suggestion)) {
                userRepository.insertSuggestion(
                    SearchSuggestion(
                        userId = loggedInUser.toInt(),
                        suggestion = suggestion,
                        timestamp = timeStamp
                    )
                )
            }
        }
    }

    fun removeSuggestion(suggestion: SearchSuggestion) {
        viewModelScope.launch {
            userRepository.removeSuggestion(suggestion)
        }
    }

    fun getUserAddresses(userId: Int): LiveData<AddressGroupWithAddress> =
        userRepository.getUserAddresses(userId)

//    fun getUserAddressesWithException(userId: Int, addressIds: List<Int>): LiveData<AddressGroupWithAddress> =
//        userRepository.getUserAddressesWithException(userId, addressIds)

    fun setAddress(addressId: Int) {
        viewModelScope.launch {
            address.postValue(userRepository.getAddressById(addressId))
        }
    }

    fun getAddress(): LiveData<Address> = address

    fun getAddressGroupWithAddresses(userId: Int): LiveData<List<AddressGroupWithAddress>> =
        userRepository.getAddressGroupWithAddresses(userId)

    fun getAddressGroupWithAddresses(
        userId: Int,
        addressGroupId: Int
    ): LiveData<AddressGroupWithAddress> =
        userRepository.getAddressGroupWithAddresses(userId, addressGroupId)

    fun getAddressGroupWithAddressByAddressId(
        userId: Int,
        addressGroupId: Int,
        addressId: Int
    ): LiveData<AddressGroupWithAddress> =
        userRepository.getAddressGroupWithAddressByAddressId(userId, addressGroupId, addressId)

    fun getAddressGroupId(): LiveData<Int> = addressGroupId

    fun getAddressId(): LiveData<Int> = addressId

    fun insertAddress(address: Address, addressGroupName: String) {
        viewModelScope.launch {
            val id = userRepository.insertAddress(address)
//            addressId.postValue(id.toInt())
            if (addressGroupName != "default") {
                insertIntoAddressAndGroupCrossRef(id.toInt(), addressGroupName)
            }
            insertIntoAddressAndGroupCrossRef(id.toInt(), "default")
        }
    }

    fun insertIntoAddressGroup(addressGroup: AddressGroup) {
        viewModelScope.launch {
            val id = userRepository.insertAddressGroup(addressGroup)
            addressGroupId.postValue(id.toInt())
        }
    }

    fun updateAddressGroupName(addressGroupId: Int, groupName: String) {
        viewModelScope.launch {
            userRepository.updateAddressGroupName(addressGroupId, groupName)
        }
    }

    fun deleteAddressGroup(addressGroupId: Int) {
        viewModelScope.launch {
            userRepository.deleteAddressGroup(addressGroupId)
        }
    }

    fun insertIntoAddressAndGroupCrossRef(addressId: Int, addressGroupName: String) =
        viewModelScope.launch {
            userRepository.insertAddressAndGroupCrossRef(
                getLoggedInUser().toInt(),
                addressId,
                addressGroupName
            )
        }

    fun deleteAddressFromGroup(addressId: Int, addressGroupId: Int) {
        viewModelScope.launch {
            userRepository.deleteAddressFromGroup(addressId, addressGroupId)
        }
    }

    fun deleteAddress(addressId: Int) {
        viewModelScope.launch { userRepository.deleteAddress(addressId) }
    }

    suspend fun insertOrder(order: Order):Int {
        val orderId = viewModelScope.async(Dispatchers.IO) {
            return@async userRepository.insertOrder(order)
        }
        return orderId.await().toInt()
    }

    fun insertProductOrdered(productOrdered: ProductOrdered) {
        viewModelScope.launch { userRepository.insertProductOrdered(productOrdered) }
    }

}