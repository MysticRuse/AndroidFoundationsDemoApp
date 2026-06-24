package com.sample.android.essentialcompose.livecodingsamples

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Problem 6: Drag-to-Reorder List
 * Build a vertical list of items that the user can long-press and drag to reorder. The list should visually update in real time as the item is dragged over others.
 * Requirements:
 * Long-press to initiate drag
 * Visual feedback: dragged item is elevated or highlighted
 * List reorders in real time as the item crosses other items
 * Smooth animation for non-dragged items shifting position
 * Interviewer follow-ups to consider:
 * How would you persist with the new order?
 * How do you handle auto-scrolling when dragging near the list edge?
 * Is this feasible in vanilla Compose or do you need a library?
 *
 * ── ARCHITECTURE ──────────────────────────────────────────────────────────────
 * ViewModel owns the list (items: StateFlow). Drag state (draggedIndex,
 * dragOffsetY) lives in local composable `remember` — NOT the ViewModel.
 *
 * WHY: StateFlow writes are asynchronous. Reading state.dragOffsetY inside a
 * gesture lambda gives the value from the PREVIOUS recomposition, not the current
 * frame. Local `mutableStateOf` reads/writes are synchronous — no lag.
 *
 * ── GESTURE FLOW ──────────────────────────────────────────────────────────────
 * Long press → onDragStart: set draggedIndex, reset dragOffsetY to 0
 * Drag       → dragOffsetY += delta (local, immediate)
 *            → detectSwap: is dragged item's center inside a neighbor's bounds?
 *            → if yes: adjust dragOffsetY by ±itemHeight (keeps item under finger)
 *                      update draggedIndex to new position
 *                      call viewModel.moveItem(from, to) to reorder the list
 * Release    → draggedIndex = null, dragOffsetY = 0 → item is already in its
 *              new layout slot, so no snap-back occurs
 *            → viewModel.saveOrder() to persist
 *
 * ── VISUAL ────────────────────────────────────────────────────────────────────
 * graphicsLayer { translationY = dragOffsetY } moves pixels without affecting layout,
 * so other items don't shift to fill the gap during drag.
 * zIndex(1f) on the dragged item renders it above neighbors.
 * animateItem() on each item smoothly animates neighbors into their new positions.
 * CardDefaults.cardElevation + scale via animateFloatAsState give the "lifted" look.
 *
 * ── SWAP DETECTION ────────────────────────────────────────────────────────────
 * Uses LazyListState.layoutInfo.visibleItemsInfo to get live item positions.
 * Compares dragged item's visual center (layout offset + size/2 + dragOffsetY)
 * against each neighbor's bounds. Swaps when the center crosses into a neighbor.
 * After swap, offset is adjusted by ±itemHeight to prevent immediate reverse swap.
 *
 * ── INTERVIEW FOLLOW-UPS ──────────────────────────────────────────────────────
 * Persist order:   call repository.saveOrder() in onDragEnd (Room/API inside impl).
 * Auto-scroll:     in onDrag, check if drag Y is near the list viewport edge,
 *                  then call listState.scrollBy(amount) in a coroutineScope.
 * Library vs DIY:  vanilla Compose works (as shown), but `sh.calvin.reorderable`
 *                  adds battle-tested auto-scroll, haptics, and accessibility for free.
 */

// ─── 1. Data Model ────────────────────────────────────────────────────────────

data class DragItem(val id: Int, val title: String)

// ─── 2. Repository ────────────────────────────────────────────────────────────

interface DragDropRepository {
    fun getItems(): List<DragItem>
    fun saveOrder(items: List<DragItem>)
}

class DragDropRepositoryImpl : DragDropRepository {

    private val dragItems = mutableListOf(
        DragItem(1, "Buy groceries"),
        DragItem(2, "Call the dentist"),
        DragItem(3, "Finish project report"),
        DragItem(4, "Walk the dog"),
        DragItem(5, "Read for 30 minutes"),
        DragItem(6, "Reply to emails"),
        DragItem(7, "Prepare lunch"),
        DragItem(8, "Team standup at 10am"),
        DragItem(9, "Review pull requests"),
        DragItem(10, "Pay electricity bill")
    )

    override fun getItems(): List<DragItem> = dragItems.toList()

    override fun saveOrder(items: List<DragItem>) {
        dragItems.clear()
        dragItems.addAll(items)
        println("New order saved: ${items.map { it.id }}")
    }
}

// ─── 3. Hilt Module ───────────────────────────────────────────────────────────

@Module
@InstallIn(SingletonComponent::class)
object DragDropModule {
    @Provides
    @Singleton
    fun provideDragDropRepository(): DragDropRepository = DragDropRepositoryImpl()
}

// ─── 4. ViewModel — owns the list only ───────────────────────────────────────

@HiltViewModel
class DragDropViewModel @Inject constructor(
    private val repository: DragDropRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<DragItem>>(emptyList())
    val items: StateFlow<List<DragItem>> = _items.asStateFlow()

    init {
        _items.update { repository.getItems() }
    }

    fun moveItem(from: Int, to: Int) {
        _items.update { current ->
            current.toMutableList().apply { add(to, removeAt(from)) }
        }
    }

    fun saveOrder() {
        repository.saveOrder(_items.value)
    }
}

// ─── 5. Screen ────────────────────────────────────────────────────────────────

@Composable
fun DragToReorderListScreen(viewModel: DragDropViewModel = hiltViewModel()) {
    val items by viewModel.items.collectAsStateWithLifecycle()

    DragDropList(
        items = items,
        onMove = viewModel::moveItem,
        onDragEnd = viewModel::saveOrder
    )
}

// ─── 6. List — drag state lives here ─────────────────────────────────────────

@Composable
fun DragDropList(
    items: List<DragItem>,
    onMove: (from: Int, to: Int) -> Unit,
    onDragEnd: () -> Unit
) {
    val listState = rememberLazyListState()

    // Drag state is local — reads/writes inside gesture lambdas are synchronous.
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(items = items, key = { _, item -> item.id }) { index, item ->
            DraggableItem(
                modifier = Modifier.animateItem(),
                text = item.title,
                isDragging = index == draggedIndex,
                dragOffsetY = if (index == draggedIndex) dragOffsetY else 0f,
                onDragStart = {
                    draggedIndex = index
                    dragOffsetY = 0f
                },
                onDrag = { delta ->
                    dragOffsetY += delta

                    val current = draggedIndex ?: return@DraggableItem
                    detectSwap(
                        listState = listState,
                        draggedIndex = current,
                        dragOffsetY = dragOffsetY
                    ) { from, to, itemHeight ->
                        // Adjust offset so item stays under the finger after swapping
                        dragOffsetY += if (to > from) -itemHeight.toFloat() else itemHeight.toFloat()
                        draggedIndex = to
                        onMove(from, to)
                    }
                },
                onDragEnd = {
                    draggedIndex = null
                    dragOffsetY = 0f
                    onDragEnd()
                }
            )
        }
    }
}

// ─── 7. Draggable Item ────────────────────────────────────────────────────────

@Composable
fun DraggableItem(
    modifier: Modifier = Modifier,
    text: String,
    isDragging: Boolean,
    dragOffsetY: Float,
    onDragStart: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit
) {
    val elevation by animateDpAsState(
        targetValue = if (isDragging) 16.dp else 2.dp,
        label = "elevation"
    )
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1f,
        label = "scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(if (isDragging) 1f else 0f)
            .graphicsLayer {
                translationY = dragOffsetY
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDragStart() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.y)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragEnd
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Drag handle",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(12.dp))
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// ─── 8. Swap Detection ────────────────────────────────────────────────────────

fun detectSwap(
    listState: LazyListState,
    draggedIndex: Int,
    dragOffsetY: Float,
    onSwap: (from: Int, to: Int, itemHeight: Int) -> Unit
) {
    val layoutInfo = listState.layoutInfo
    val draggedItem = layoutInfo.visibleItemsInfo
        .firstOrNull { it.index == draggedIndex } ?: return

    val draggedCenter = draggedItem.offset + draggedItem.size / 2 + dragOffsetY

    val target = layoutInfo.visibleItemsInfo.firstOrNull { info ->
        info.index != draggedIndex &&
        draggedCenter > info.offset &&
        draggedCenter < info.offset + info.size
    }

    if (target != null) {
        onSwap(draggedIndex, target.index, draggedItem.size)
    }
}
