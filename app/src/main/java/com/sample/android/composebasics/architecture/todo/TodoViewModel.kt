package com.sample.android.composebasics.architecture.todo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TodoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<TodoUiState>(TodoUiState.Loading)
    val uiState: StateFlow<TodoUiState> = _uiState

    var nextId = 0

    fun addTodo(title: String) {
        if (title.isBlank()) return

        _uiState.update { currentState ->
            val newItem = TodoItem(id = (nextId++).toString(), title = title)
            when (currentState) {
                is TodoUiState.Success -> currentState.copy(items = currentState.items + newItem)
                else -> TodoUiState.Success(listOf(newItem))
            }
        }
    }

    fun removeTodo(id: String) {
        _uiState.update { currentState ->
            if (currentState is TodoUiState.Success) {
                currentState.copy(items = currentState.items.filterNot { it.id == id })
            } else {
                currentState
            }
        }
    }

    fun toggleTodo(id: String) {
        _uiState.update { currentState ->
            if (currentState is TodoUiState.Success) {
                val updatedList = currentState.items.map {
                    if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
                }
                currentState.copy(items = updatedList)
            } else {
                currentState
            }
        }
    }
}

sealed class TodoUiState {
    object Loading: TodoUiState()
    data class Success(val items: List<TodoItem>) : TodoUiState()
    data class Error(val message: String) : TodoUiState()
}
