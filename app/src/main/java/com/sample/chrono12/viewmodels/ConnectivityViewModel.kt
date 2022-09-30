package com.sample.chrono12.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectivityViewModel @Inject constructor(
    context: Application
) : ViewModel() {

    private val _networkState = MutableLiveData<Boolean>()
    val networkState: LiveData<Boolean>
        get() = _networkState

    private var hasInternet = false

    private var networkCallback: ConnectivityManager.NetworkCallback
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    init {
        networkCallback = getNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        viewModelScope.launch {
            delay(1000)
            setState()
        }
    }

    override fun onCleared() {
        super.onCleared()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun getNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)

            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val hasInternetCapability =
                networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

            if (hasInternetCapability == true) {
                hasInternet = true
                setState()
            }
        }


        override fun onLost(network: Network) {
            super.onLost(network)
            hasInternet = false
            setState()
        }
    }

    private fun setState() {
        _networkState.postValue(hasInternet)
    }

    fun setNetWorkState(state: Boolean){
        _networkState.value = state
    }
}