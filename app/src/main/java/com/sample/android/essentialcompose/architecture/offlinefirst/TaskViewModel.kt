package com.sample.android.essentialcompose.architecture.offlinefirst

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<TaskUiState> = combine(
        repository.tasks,
        _isLoading,
        _errorMessage
    ) { tasks, isLoading, errorMessage ->
        TaskUiState(tasks, isLoading, errorMessage)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState(isLoading = true)
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.refreshTasks()
            } catch (e: Exception) {
                // Requirement: Show error only if both cache and network fail
                // We use first() to get a snapshot of the current local tasks
                val currentTasks = repository.tasks.first()
                if (currentTasks.isEmpty()) {
                    _errorMessage.value = "Failed to load tasks. Please check your connection."
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }
}
