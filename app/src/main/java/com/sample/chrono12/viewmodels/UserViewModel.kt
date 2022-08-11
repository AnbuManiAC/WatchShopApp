package com.sample.chrono12.viewmodels

import androidx.lifecycle.ViewModel
import com.sample.chrono12.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

}