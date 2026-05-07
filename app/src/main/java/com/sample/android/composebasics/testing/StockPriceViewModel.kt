package com.sample.android.composebasics.testing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface StockRepository {
    fun priceUpdates(): Flow<Double>
}

class StockPriceViewModel(
    private val repository: StockRepository
) : ViewModel() {
    private val _price = MutableStateFlow(0.0)
    val price: StateFlow<Double> = _price

    fun startTracking() {
        viewModelScope.launch {
            repository.priceUpdates().collect {
                _price.value = it
            }
        }
    }
}
