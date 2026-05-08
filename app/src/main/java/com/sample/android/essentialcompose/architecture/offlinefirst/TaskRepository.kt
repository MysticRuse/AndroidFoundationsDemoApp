package com.sample.android.essentialcompose.architecture.offlinefirst

import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskApi: TaskApi,
    private val taskDao: TaskDao
) {
    // Single Source of Truth: Exposed as a Flow directly from the DAO
    val tasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun refreshTasks() {
        try {
            // 1. Fetch from network
            val remoteTasks = taskApi.fetchTasks()
            
            // 2. Update local database (Single Source of Truth)
            // In a real app, you might want to sync instead of deleting all
            taskDao.deleteAllTasks()
            taskDao.insertTasks(remoteTasks)
        } catch (e: Exception) {
            // Rethrow or handle based on your error policy
            throw e
        }
    }

    suspend fun toggleTaskCompletion(task: Task) {
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        taskDao.updateTask(updatedTask)
        // Note: We don't necessarily need to push to network immediately in an offline-first app,
        // but can add that logic here.
    }
}
