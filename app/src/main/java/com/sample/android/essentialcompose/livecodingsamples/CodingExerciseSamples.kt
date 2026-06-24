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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

enum class LiveCodingSamples(val title: String) {
    EXPANDABLE_LIST_MULTIPLE("Expandable List - Multiple"),
    EXPANDABLE_LIST_SINGLE("Expandable List - Single"),
    DEBOUNCED_SEARCH_BAR("Debounced Search Bar"),
    INFINITE_COUNTER_WITH_UNDO("Infinite Counter with Undo"),

    MULTI_SELECT_CHIP_GROUP("Multi Select Chip Group"),
    SWIPE_TO_DISMISS_LIST_ITEM("Swipe to Dismiss List Item"),

    PAGINATED_LIST_WITH_LOADING_FOOTER("Paginated List"),

    DRAG_TO_REORDER_LIST("Drag to Reorder List"),
}

@Composable
fun CodingExercisesScreen() {

    var currentSample by rememberSaveable { mutableStateOf<LiveCodingSamples?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentSample) {
            null -> LiveCodingSamplesMenu { currentSample = it }
            LiveCodingSamples.EXPANDABLE_LIST_MULTIPLE -> FaqExpandableListScreen()
            LiveCodingSamples.EXPANDABLE_LIST_SINGLE -> FaqExpandableListScreenExpandOne()
            LiveCodingSamples.DEBOUNCED_SEARCH_BAR -> {
                val viewModel: CityViewModel = viewModel(
                    factory = CityViewModelFactory(LocalContext.current)
                )
                SearchListScreen(viewModel)
            }
            LiveCodingSamples.INFINITE_COUNTER_WITH_UNDO -> InfiniteCounterWithUndoScreen()
            LiveCodingSamples.MULTI_SELECT_CHIP_GROUP -> MultiSelectChipChipScreen()
            LiveCodingSamples.SWIPE_TO_DISMISS_LIST_ITEM  -> SwipeToDismissListItemScreen()
            LiveCodingSamples.PAGINATED_LIST_WITH_LOADING_FOOTER -> PaginatedListScreen()
            LiveCodingSamples.DRAG_TO_REORDER_LIST -> DragToReorderListScreen()
        }
    }

}

@Composable
fun LiveCodingSamplesMenu(onSelect: (LiveCodingSamples) -> Unit) {
    val samples = listOf(
        LiveCodingSamples.EXPANDABLE_LIST_MULTIPLE,
        LiveCodingSamples.EXPANDABLE_LIST_SINGLE,
        LiveCodingSamples.DEBOUNCED_SEARCH_BAR,
        LiveCodingSamples.INFINITE_COUNTER_WITH_UNDO,
        LiveCodingSamples.MULTI_SELECT_CHIP_GROUP,
        LiveCodingSamples.SWIPE_TO_DISMISS_LIST_ITEM,
        LiveCodingSamples.PAGINATED_LIST_WITH_LOADING_FOOTER,
        LiveCodingSamples.DRAG_TO_REORDER_LIST
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
