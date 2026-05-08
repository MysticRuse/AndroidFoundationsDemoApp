package com.sample.android.essentialcompose.lifecycleandandroidcomponents.savedstatehandle

import kotlinx.coroutines.delay

class SearchRepository {
    private val allItems = listOf(
        "Android Jetpack",
        "Compose UI",
        "Kotlin Coroutines",
        "State Management",
        "ViewModel & LiveData",
        "SavedStateHandle",
        "Lifecycle Components",
        "Room Database",
        "Navigation Component",
        "WorkManager"
    )

    suspend fun search(query: String): List<String> {
        if (query.isBlank()) return emptyList()
        delay(1000) // Simulate network/DB delay
        return allItems.filter { it.contains(query, ignoreCase = true) }
    }
}