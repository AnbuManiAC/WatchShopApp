package com.sample.chrono12.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chrono12.data.entities.SearchSuggestion
import com.sample.chrono12.data.entities.User
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.data.models.Response
import com.sample.chrono12.data.models.UserDetails
import com.sample.chrono12.data.models.UserField
import com.sample.chrono12.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.reflect.Field
import java.net.PasswordAuthentication
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

    fun clearUserFieldInfo(){
        null.also { userField.value = it }
    }

    fun getIsUserLoggedIn(): Boolean = isUserLoggedIn

    fun getLoggedInUser(): Long = loggedInUser

    fun setLoggedInUSer(userId: Long){
        loggedInUser = userId
        isUserLoggedIn = true
        setUserDetails(userId)
    }

    private fun setUserDetails(userId: Long) {
        viewModelScope.launch {
            val user: User = userRepository.getUser(userId)
            userDetails.value = UserDetails(user.name, user.email, user.mobileNumber )
        }
    }

    fun getUserDetails(): LiveData<UserDetails> = userDetails

    fun authenticateUser(emailId: String, password: String) = viewModelScope.launch{
        if(isExistingEmail(emailId)){
            if(validatePassword(emailId, password)){
                val userId = userRepository.getUserId(emailId)
                setLoggedInUSer(userId)
                userField.value = UserField.ALL.also {
                    it.response = Response.SUCCESS.also { it.message = "Logged in successfully" }
                }
            }
            else{
                userField.value =UserField.PASSWORD.also {
                    it.response = Response.FAILURE.also { it.message = "Incorrect password" }
                }
            }
        }
        else{
            userField.value = UserField.EMAIL.also {
                it.response = Response.FAILURE.also { it.message = "Email does not exists" }
            }
        }
    }

    fun initiateSignUp(user: User) = viewModelScope.launch {
        if(isExistingEmail(user.email)){
            userField.value = UserField.EMAIL.also {
                it.response = Response.FAILURE.also { it.message = "Email id already Exists" }
            }
            return@launch
        }
        createUser(user)
    }

    private fun createUser(user: User) = viewModelScope.launch {
        setLoggedInUSer(userRepository.createUser(user))
        userField.value = UserField.ALL.also {
            it.response = Response.SUCCESS.also { it.message = "Sign up  successfull" }
        }
    }

    private suspend fun validatePassword(emailId: String, password: String): Boolean =
        userRepository.validatePassword(emailId, password) == 1

    private suspend fun isExistingEmail(emailId: String): Boolean =
        userRepository.isExistingEmail(emailId) == 1

    fun getUserFieldInfo(): LiveData<UserField> = userField

    fun logOutUser(){
        loggedInUser = 0
        isUserLoggedIn = false
    }

    fun setSuggestions(){
        viewModelScope.launch {
            suggestions.postValue(userRepository.getSuggestions(loggedInUser.toInt()))
        }
    }

    fun getSuggestions(): LiveData<List<SearchSuggestion>> = suggestions

    fun insertSuggestion(suggestion: String, timeStamp: Long){
        viewModelScope.launch{
            if (isUserLoggedIn && !userRepository.isSuggestionPresent(suggestion)){
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

    fun removeSuggestion(suggestion: SearchSuggestion){
        viewModelScope.launch{
            userRepository.removeSuggestion(suggestion)
        }
    }

    fun getUserAddresses(userId: Int): LiveData<AddressGroupWithAddress> =
        userRepository.getUserAddresses(userId)

    fun getAddressGroupWithAddresses(userId: Int): LiveData<List<AddressGroupWithAddress>> =
        userRepository.getAddressGroupWithAddresses(userId)

}