package com.sample.android.essentialcompose.livecodingsamples

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

enum class LiveCodingSamples(val title: String) {
    EXPANDABLE_LIST_MULTIPLE("Expandable List - Multiple"),
    EXPANDABLE_LIST_SINGLE("Expandable List - Single")
}
@Composable
fun CodingExercisesScreen() {

    var currentSample by rememberSaveable { mutableStateOf<LiveCodingSamples?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentSample) {
            null -> LiveCodingSamplesMenu { currentSample = it }
            LiveCodingSamples.EXPANDABLE_LIST_MULTIPLE -> FaqExpandableListScreen()
            LiveCodingSamples.EXPANDABLE_LIST_SINGLE -> FaqExpandableListScreenExpandOne()
        }
    }

}

@Composable
fun LiveCodingSamplesMenu(onSelect: (LiveCodingSamples) -> Unit) {
    val samples = listOf(
        LiveCodingSamples.EXPANDABLE_LIST_MULTIPLE,
        LiveCodingSamples.EXPANDABLE_LIST_SINGLE
    )
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(samples) { sample ->
            ListItem(
                headlineContent = { Text(sample.title) },
                modifier = Modifier.clickable { onSelect(sample) }
            )
            HorizontalDivider()
        }
    }
}
