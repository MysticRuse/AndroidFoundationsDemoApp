package com.sample.android.essentialcompose.lifecycleandandroidcomponents.savedstatehandle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class SearchViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: SearchRepository
) : ViewModel() {

    // Persist search query across process death.
    // Key "search_query" is saved/restored by System.
    val searchQuery = savedStateHandle.getStateFlow("search_query", "")

    // Persist scroll position across process death.
    var scrollIndex: Int
        get() = savedStateHandle["scroll_index"] ?: 0
        set(value) { savedStateHandle["scroll_index"] = value }

    // Search results: Automatically re-fetches when query is restored from process death.
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<String>> = searchQuery
        .flatMapLatest { query ->
            flow {
                if (query.isBlank()) {
                    emit(emptyList<String>())
                } else {
                    emit(repository.search(query))
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onQueryChanged(newQuery: String) {
        savedStateHandle["search_query"] = newQuery
    }
}
