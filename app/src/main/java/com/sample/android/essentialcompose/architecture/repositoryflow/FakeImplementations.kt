package com.sample.android.essentialcompose.architecture.repositoryflow

import kotlinx.coroutines.delay

class FakeUserApi : UserApi {
    override suspend fun fetchUserById(id: String): User {
        delay(2000) // Simulate network delay
        return User(id = id, name = "Network User $id", email = "network$id@example.com")
    }
}

class FakeUserDao : UserDao {
    private var cachedUser: User? = null

    override fun getUserById(id: String): User? {
        return cachedUser
    }

    override fun insertUser(user: User) {
        cachedUser = user
    }
}
