package com.sample.chrono12.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chrono12.data.entities.Address
import com.sample.chrono12.data.entities.AddressGroup
import com.sample.chrono12.data.entities.SearchSuggestion
import com.sample.chrono12.data.entities.User
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.data.models.Response
import com.sample.chrono12.data.models.UserDetails
import com.sample.chrono12.data.models.UserField
import com.sample.chrono12.data.repository.UserRepository
import com.sample.chrono12.data.models.ProfileSettingAction
import dagger.hilt.android.lifecycle.HiltViewModel
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
    var suggestedLoginEmail = ""
    var suggestedSignupEmail = ""
    private val _suggestions = MutableLiveData<List<SearchSuggestion>>()
    val suggestion: LiveData<List<SearchSuggestion>>
        get() = _suggestions

    private val address = MutableLiveData<Address>()
    private var addressId = MutableLiveData<Int>()
    private var addressGroupId = MutableLiveData<Int>()
    private val addressIds = MutableLiveData<List<Int>>()
    private var profileSettingAction = MutableLiveData<ProfileSettingAction>()

    fun setProfileSettingAction(action: ProfileSettingAction) {
        profileSettingAction.value = action
    }

    fun getProfileSettingAction(): LiveData<ProfileSettingAction> = profileSettingAction

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
            userDetails.value = UserDetails(user.name, user.email, user.mobileNumber, user.image)
        }
    }

    fun deleteProfilePicture(userId: Int) {
        viewModelScope.launch {
            userRepository.deleteProfilePicture(userId)
            setUserDetails(userId.toLong())
        }
    }

    fun getUserDetails(): LiveData<UserDetails> = userDetails

    fun authenticateUser(emailId: String, password: String) = viewModelScope.launch {
        if (isExistingEmail(emailId)) {
            if (validatePassword(emailId, password)) {
                val userId = userRepository.getUserId(emailId)
                setLoggedInUser(userId)
                userField.value = UserField.ALL.also { userField ->
                    userField.response =
                        Response.SUCCESS.also { it.message = "Logged in successfully" }
                }
            } else {
                userField.value = UserField.PASSWORD.also { userField ->
                    userField.response = Response.FAILURE.also { it.message = "Incorrect password" }
                }
            }
        } else {
            userField.value = UserField.EMAIL.also { userField ->
                suggestedSignupEmail = emailId
                userField.response = Response.FAILURE.also { it.message = "Email does not exists" }
            }
        }
    }

    fun initiateSignUp(user: User) = viewModelScope.launch {
        if (!isExistingEmail(user.email)) {
            if (isExistingMobileNumber(user.mobileNumber)) {
                userField.value = UserField.MOBILE.also { userField ->
                    userField.response =
                        Response.FAILURE.also { it.message = "Mobile Number already Exists" }
                }
            } else {
                createUser(user)
            }
        } else {
            userField.value = UserField.EMAIL.also { userField ->
                userField.response =
                    Response.FAILURE.also { it.message = "Email id already Exists" }
                suggestedLoginEmail = user.email
            }
        }
    }

    private fun createUser(user: User) = viewModelScope.launch {
        setLoggedInUser(userRepository.createUser(user))
        userField.value = UserField.ALL.also { userField ->
            userField.response = Response.SUCCESS.also { it.message = "Sign up successful" }
        }
    }

    fun addProfilePicture(imagePath: String, userId: Int) = viewModelScope.launch {
        userRepository.addProfilePicture(imagePath, userId)
        setUserDetails(userId.toLong())
    }

    private suspend fun validatePassword(emailId: String, password: String): Boolean =
        userRepository.validatePassword(emailId, password) == 1

    private suspend fun isExistingEmail(emailId: String): Boolean =
        userRepository.isExistingEmail(emailId) == 1

    private suspend fun isExistingMobileNumber(mobile: String): Boolean =
        userRepository.isExistingMobile(mobile) == 1

    fun getUserFieldInfo(): LiveData<UserField> = userField

    fun logOutUser() {
        loggedInUser = 0
        isUserLoggedIn = false
    }

    fun setSearchHistory() {
        viewModelScope.launch {
            _suggestions.postValue(userRepository.getSearchHistory(loggedInUser.toInt()))
        }
    }

    fun updateSearchSuggestion(searchString: String) {
        viewModelScope.launch {
            _suggestions.postValue(
                userRepository.getSearchSuggestion(
                    getQueryAsList(searchString),
                    loggedInUser.toInt()
                )
            )
        }
    }

    private fun getQueryAsList(query: String): List<String> {
        val list = query.split(" ", ",", ", ", " ,", "'")
        val searchQuery = ArrayList<String>()
        list.forEach {
            searchQuery.add("%$it%")
        }
        return searchQuery
    }

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
            setSearchHistory()
        }
    }

    fun getUserAddresses(userId: Int): LiveData<AddressGroupWithAddress> =
        userRepository.getUserAddresses(userId)

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

    fun getAddressGroupName(userId: Int, addressGroupId: Int): LiveData<String> =
        userRepository.getAddressGroupName(userId, addressGroupId)


    fun insertAddress(address: Address, addressGroupName: String) {
        viewModelScope.launch {
            val id = userRepository.insertAddress(address)
            if (addressGroupName != "default") {
                insertIntoAddressAndGroupCrossRef(id.toInt(), addressGroupName)
            }
            insertIntoAddressAndGroupCrossRef(id.toInt(), "default")
        }
    }

    private fun setAddressGroupId(id:Int){
        addressGroupId.value = id
    }

    suspend fun insertIntoAddressGroup(addressGroup: AddressGroup): Int  {
        val id = userRepository.insertAddressGroup(addressGroup)
        setAddressGroupId(id.toInt())
        return id.toInt()
    }




    suspend fun checkForAddressGroupExistence(groupName: String) =
        userRepository.isExistingAddressGroup(groupName, getLoggedInUser().toInt()) == 1


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

    fun deleteSearchHistory(userId: Int) {
        viewModelScope.launch {
            userRepository.deleteSearchHistory(userId)
        }
    }
}