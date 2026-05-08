package com.sample.android.essentialcompose.architecture.offlinefirst

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeTaskApi : TaskApi {
    override suspend fun fetchTasks(): List<Task> {
        delay(1500) // Simulate network delay
        return listOf(
            Task("1", "Learn Compose", "Master the basics of state"),
            Task("2", "Implement Repository", "Connect cache, DB, and API"),
            Task("3", "Master Offline-First", "Single source of truth pattern")
        )
    }
}

class FakeTaskDao : TaskDao {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    
    override fun getAllTasks(): Flow<List<Task>> = _tasks

    override suspend fun insertTasks(tasks: List<Task>) {
        _tasks.value = tasks
    }

    override suspend fun updateTask(task: Task) {
        _tasks.update { list ->
            list.map { if (it.id == task.id) task else it }
        }
    }

    override suspend fun deleteAllTasks() {
        _tasks.value = emptyList()
    }
}
