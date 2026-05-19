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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


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
fun MultiSelectChipChipScreen() {
    val allTags = remember { listOf("Kotlin", "Java", "Swift", "Dart", "Python", "C++", "Go") }

    // Caller owns the state.
    // Identity vs Equality: If the tags were objects instead of strings, you'd mention that the
    // Set relies on equals() and hashCode() for the toggle logic to work correctly.
    var selectedTags by rememberSaveable { mutableStateOf(setOf<String>())}

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select Programming Languages", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        MultiSelectChipGroup(
            tags = allTags,
            selectedTags = selectedTags,
            onToggleTag = { selectedTags = it }
        )

        // Displaying results to prove callback worked.
        Text("Selected: ${selectedTags.joinToString()}")
    }
}