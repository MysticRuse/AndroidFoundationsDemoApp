package com.sample.android.composebasics.architecture.todo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * An input field to create new tasks.
 * List of tasks
 * Toggle completion in each task
 * Delete task.
 *
 * It uses collectAsStateWithLifecycle() for lifecycle-aware state collection.
 */
@Composable
fun TodoScreen(
    viewModel: TodoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Todo List",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AddTodoSection(onAddTodo = viewModel::addTodo)

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is TodoUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is TodoUiState.Success -> {
                    TodoList(
                        items = state.items,
                        onToggleTodo = viewModel::toggleTodo,
                        onRemoveTodo = viewModel::removeTodo
                    )
                }
                is TodoUiState.Error -> {
                    Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AddTodoSection(onAddTodo: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Enter a new todo") }
        )
        IconButton(onClick = {
            if (text.isNotBlank()) {
                onAddTodo(text)
                text = ""
            }
        }) {
            Icon(Icons.Default.Add, contentDescription = "Add Todo")
        }
    }
}

@Composable
fun TodoList(
    items: List<TodoItem>,
    onToggleTodo: (String) -> Unit,
    onRemoveTodo: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { item ->
            TodoListItem(
                item = item,
                onToggle = { onToggleTodo(item.id) },
                onRemove = { onRemoveTodo(item.id) }
            )
        }
    }
}

@Composable
fun TodoListItem(
    item: TodoItem,
    onToggle: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Text(
                text = item.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Todo")
            }
        }
    }
}
