package com.sample.android.essentialcompose.architecture.offlinefirst

interface TaskApi {
    suspend fun fetchTasks(): List<Task>
}
