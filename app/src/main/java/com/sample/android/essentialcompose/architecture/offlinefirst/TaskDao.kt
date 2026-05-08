package com.sample.android.essentialcompose.architecture.offlinefirst

import kotlinx.coroutines.flow.Flow

interface TaskDao {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun insertTasks(tasks: List<Task>)
    suspend fun updateTask(task: Task)
    suspend fun deleteAllTasks()
}
