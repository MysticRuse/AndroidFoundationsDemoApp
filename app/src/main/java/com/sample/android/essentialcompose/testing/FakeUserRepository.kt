package com.sample.android.essentialcompose.testing

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

interface UserRepository {
    suspend fun getUser(userId: String): User
    suspend fun saveUser(user: User)

    fun observeUser(id: String): Flow<User>
}

class FakeUserRepository: UserRepository {
    // In-memory storage using a StateFlow of a Map
    private val usersState = MutableStateFlow<Map<String, User>>(emptyMap())

    // Test control properties
    var shouldReturnError = false
    var delayMillis = 0L

    /**
     * Simulate a network delay and error conditions.
     */
    private suspend fun simulateNetworkDelay() {
        if (delayMillis > 0) {
            delay(delayMillis)
        }

        if (shouldReturnError) {
            throw Exception("Simulated Network error")
        }
    }

    override suspend fun getUser(userId: String): User {
        simulateNetworkDelay()
        return usersState.value[userId] ?: throw Exception("User not found")
    }

    override suspend fun saveUser(user: User) {
        simulateNetworkDelay()
        usersState.update { currentUsers ->
            currentUsers + (user.id to user)
        }
    }

    override fun observeUser(id: String): Flow<User> {
        return usersState
            .map { it[id] }
            .filterNotNull()
    }
}