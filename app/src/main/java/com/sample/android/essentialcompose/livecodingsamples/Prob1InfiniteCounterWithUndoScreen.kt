package com.sample.android.essentialcompose.livecodingsamples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InfiniteCounterViewModel() : ViewModel() {

    // Current State
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    // History
    private val history = mutableListOf<Int>()

    // State to enable/disable Undo button
    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()


    fun increment() {
        addToHistory(count.value)
        _count.value++
    }

    fun decrement() {
        addToHistory(count.value)
        _count.value--
    }

    fun undo() {
        if (history.isNotEmpty()) {
            val lastValue = history.removeAt(history.size - 1)
            _count.value = lastValue
            _canUndo.value = history.isNotEmpty()
        }
    }

    private fun addToHistory(value: Int) {
        history.add(value)
        _canUndo.value = true
    }
}

@Composable
fun InfiniteCounterWithUndoScreen(viewModel: InfiniteCounterViewModel = viewModel()) {

    val count by viewModel.count.collectAsStateWithLifecycle()
    val canUndo by viewModel.canUndo.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Count: $count",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { viewModel.increment() }) {
                Text(text = "+")
            }
            Button(onClick = { viewModel.decrement() }) {
                Text(text = "-")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.undo() }, enabled = canUndo) {
            Text(text = "Undo")
        }
    }
}