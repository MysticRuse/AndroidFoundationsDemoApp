package com.sample.android.essentialcompose.livecodingsamples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * Problem 3: Multi-Select Chip Group
 * Given a list of tags (strings), render them as a horizontally wrapping set of chips. Tapping a chip toggles its selection (highlighted vs not). Expose the set of selected tags to the caller via a callback.
 * Requirements:
 * Use FlowRow or similar for wrapping layout
 * Each chip visually indicates selected vs unselected state
 * Composable should be stateless — caller owns the selected set
 * Callback provides the updated Set<String> on each toggle
 *
 * Interviewer follow-ups to consider:
 * How would you add an "All" chip that selects/deselects everything?
 * What if there are 500 tags?
 *   --> Use LazyLayouts instead of a FlowRow.
 */
@Composable
fun MultiSelectChipGroup(
    tags: List<String>,
    selectedTags: Set<String>,
    onToggleTag: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // Determine "All" state
        // Keep allSelected as a local val calculated during recomposition. Since it’s derived
        // directly from selectedTags—which is already hoisted and persisted via
        // rememberSaveable—we ensure a single source of truth.
        val allSelected = selectedTags.size == tags.size && tags.isNotEmpty()

        // Note: If calculation for allSelected were very expensive (like processing a list of 10,000 items),
        // wouldn't use rememberSaveable; instead, would use derivedStateOf.

        // Render the "All" Chip
        // If the list is empty, "All" should typically be unselected or disabled to avoid a
        // confusing UI state where an empty list is "fully selected."
        FilterChip(
            selected = allSelected,
            onClick = {
                if (allSelected) {
                    onToggleTag(emptySet())
                } else {
                    onToggleTag(tags.toSet())
                }
            },
            label = { Text("All") }
        )

        tags.forEach { tag ->
            val isSelected = tag in selectedTags

            // FilterChip provides a better experience for screen readers compared to a
            // generic Box with a clickable modifier.
            FilterChip(
                selected = isSelected,
                onClick = {
                    val newSelection = if (isSelected) selectedTags - tag
                            else selectedTags + tag
                    onToggleTag(newSelection)
                },
                label = { Text(tag) }
            )
        }
    }
}

@Composable
fun MultiSelectChipChipScreen(
    viewModel: ChipViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select Programming Languages", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        MultiSelectChipGroup(
            tags = uiState.tags,
            selectedTags = uiState.selectedTags,
            onToggleTag = { viewModel.onToggleTag(it) }
        )

        // Displaying results to prove callback worked.
        Text("Selected: ${uiState.selectedTags.joinToString()}")
    }
}

/**
 * Repository Layer
 * In a real app, this would fetch from a DAO (Room) or API (Retrofit).
 * It exposes data as a Flow for reactivity.
 */
class ChipRepository {
    fun getTags(): Flow<List<String>> = flowOf(
        listOf("Kotlin", "Java", "Swift", "Dart", "Python", "C++", "Go")
    )
}

/**
 * UI State representation
 * Encapsulates everything the UI needs to render.
 */
data class ChipUiState(
    val tags: List<String> = emptyList(),
    val selectedTags: Set<String> = emptySet()
)

/**
 * ViewModel Layer
 * Hoists the state and handles business logic, making the UI easier to test.
 * Interacts with the Repository to fetch data.
 */
class ChipViewModel(
    private val repository: ChipRepository = ChipRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChipUiState())
    val uiState: StateFlow<ChipUiState> = _uiState.asStateFlow()

    init {
        fetchTags()
    }

    private fun fetchTags() {
        viewModelScope.launch {
            repository.getTags().collect { tags ->
                _uiState.update { it.copy(tags = tags) }
            }
        }
    }

    fun onToggleTag(newSelection: Set<String>) {
        _uiState.update { it.copy(selectedTags = newSelection) }
    }
}
