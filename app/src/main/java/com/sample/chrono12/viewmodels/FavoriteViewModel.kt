package com.sample.chrono12.viewmodels

import androidx.lifecycle.ViewModel
import com.sample.chrono12.data.repository.WatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val watchRepository: WatchRepository
): ViewModel() {



}