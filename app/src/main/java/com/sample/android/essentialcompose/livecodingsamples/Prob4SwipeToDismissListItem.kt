package com.sample.android.essentialcompose.livecodingsamples


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Problem 4: Swipe-to-Dismiss List Item
 * Build a LazyColumn of items. Each item can be swiped left to reveal a red "Delete" background. On full swipe, remove the item from the list with an animation.
 * Requirements:
 * LazyColumn with keyed items
 * Swipe-left gesture reveals a red background with a delete icon
 * Full swipe removes the item
 * Removal is animated (item height collapses smoothly)
 *
 * Interviewer follow-ups to consider:
 * 1. How do you implement an undo snackbar after deletion?
 * --> implemented below
 *     - Host State: Use SnackbarHostState to manage the queue and display of Snackbars.
 *     - Coroutine Scope: Since showSnackbar is a suspend function, need a rememberCoroutineScope to launch it from a non-composable callback (the delete button).
 *     - The "Undo" Logic: When an item is swiped away:
 *          i. Capture its index and the item itself before removing it.
 *          ii. Remove it from the mutableStateListOf immediately (optimistic UI).
 *          iii. Show the Snackbar.
 *          iv. If the user clicks "Undo", insert the item back at its exact original index.
 * 2. What happens if the user swipes two items rapidly?
 * --> If a user deletes three items rapidly, SnackbarHostState will queue them automatically.
 * However, "Undo" will only work for the specific Snackbar currently visible.
 */


@Composable
fun SwipeToDismissListItemScreen() {
    // Use remember + mutableStateListOf to track deletions
    val items =  remember {
        mutableStateListOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "item 6", "Item 7")
    }

    // 1. Define snackbar state and scope
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        // 2. Attach the host to the Scaffold
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items, key = { it }) { item ->
                SwipeToDismissRow(
                    item = item,
                    onDelete =  {
                        // 3. Deletion logic with undo
                        performDeletion(item, items, scope, snackbarHostState)
                    }
                )
            }
        }
    }
}

private fun performDeletion(
    item: String,
    items: MutableList<String>,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
): Boolean {

    // Save index to restore it exactly where it was
    val index = items.indexOf(item)
    if (index != -1) {
        items.removeAt(index)

        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "Item deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                items.add(index, item)
            }
        }
    }
    return true
}

@Composable
fun SwipeToDismissRow(item: String, onDelete: () -> Boolean) {

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val color = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> Color.Red
                else -> Color.Transparent
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White)
            }
        },
        modifier = Modifier.animateContentSize(),
    ) {
        ListItem(
            headlineContent = { Text(item) },
            modifier = Modifier
                .fillMaxWidth()
                // Order matters: Border -> Clip -> Background
                .border(
                    1.dp,
                    Color.LightGray,
                    RoundedCornerShape(12.dp)
                )
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}