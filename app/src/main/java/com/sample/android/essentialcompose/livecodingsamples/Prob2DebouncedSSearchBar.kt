package com.sample.android.essentialcompose.livecodingsamples

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.sample.android.essentialcompose.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

/**
 * Problem 2: Debounced Search Bar
 * Build a TextField that filters a list of 1000 city names. The filter should NOT run on every keystroke — debounce by 300ms. Show a "No results" message when the filtered list is empty.
 * Requirements:
 * Load city names from a data source (raw resource file or hardcoded list)
 * TextField with search icon and clear button
 * Filtering is debounced — only runs 300ms after the user stops typing
 * Show a loading indicator while the debounced filter is pending
 * Show an empty state with message when no cities match
 * Use LazyColumn with stable keys for the results list
 *
 * Interviewer follow-ups to consider:
 * Why debounce in the ViewModel vs. the composable?
 * What if filtering 1000 items takes 200ms? How do you avoid blocking the UI?
 * How would you highlight the matching substring in results?
 *
 */

/**
 * 1000 city names - loading in memory from file is  blocking operation - do not do it in the UI thread
 * Use repository pattern - do not load file data directly in ViewModel.
 * Use Dispatchers.IO to load data from file.
 * Use flow or suspend functions to load the data once.
 */

class CityRepository(private val context: Context) {

    private var cachedCities: List<String>? = null

    // Read from the file in res/raw and return a list of Cities
    fun loadCities(): List<String> {
        cachedCities?.let { return it }

        val cities = context.resources.openRawResource(R.raw.cities)
            .bufferedReader()
            .readLines()
            .filter { it.isNotBlank() }
        cachedCities = cities
        return cities
    }
}

class CityViewModel(
    private val repository: CityRepository,
    private val savedStateHandle: SavedStateHandle? = null
    ) : ViewModel() {
    private val _query = MutableStateFlow(savedStateHandle?.get<String>("query") ?: "")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        savedStateHandle?.set("query", newQuery)

        if (newQuery.isNotBlank()) {
            _isSearching.value = true
        }
    }

    @OptIn(FlowPreview::class)
    val filteredCities: StateFlow<List<String>> = _query
        .debounce(300L)
        .map { query ->
            if (query.isBlank()) repository.loadCities()
            else repository.loadCities().filter { it.contains(query, ignoreCase = true) }
        }
        .onEach { _isSearching.value = false }
        .flowOn(Dispatchers.IO)
        .stateIn( // The stateIn operator requires a CoroutineScope to convert a cold Flow into a StateFlow.
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

@Composable
fun SearchListScreen(viewModel: CityViewModel) {

    // Observe the query state
    val query by viewModel.query.collectAsStateWithLifecycle()

    // Observe the filtered list
    val filteredCities by viewModel.filteredCities.collectAsStateWithLifecycle()

    // Observe the searching state
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // TextField with the query.
        TextField(
            value = query,
            onValueChange = { viewModel.onQueryChange(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search cities...") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isSearching) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (query.isNotBlank() && filteredCities.isEmpty()) {
            // if no results found, show "No results"
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No results")
            }
        } else {
            // show the filtered list
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredCities) { city ->
                    ListItem(
                        headlineContent = { Text(city) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

class CityViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
        return CityViewModel(CityRepository(context), savedStateHandle) as T
    }
}
