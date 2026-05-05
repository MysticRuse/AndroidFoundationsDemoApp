package com.sample.android.composebasics.architecture.offlinefirst

interface TaskApi {
    suspend fun fetchTasks(): List<Task>
}
