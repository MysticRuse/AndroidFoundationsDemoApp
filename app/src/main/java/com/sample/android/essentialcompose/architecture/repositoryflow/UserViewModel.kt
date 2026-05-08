package com.sample.android.essentialcompose.architecture.repositoryflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userId = MutableStateFlow("1") // Demo with ID "1"

    // UI State: Observe the repository flow
    @OptIn(ExperimentalCoroutinesApi::class)
    val user: StateFlow<User?> = _userId
        .filterNotNull()
        .flatMapLatest { id -> repository.getUserById(id) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Cancels upstream after 5 s of no subscribers (survives rotation)
            initialValue = null
        )

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.refreshUser(_userId.value)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
