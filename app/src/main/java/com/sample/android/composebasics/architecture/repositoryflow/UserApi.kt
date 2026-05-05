package com.sample.android.composebasics.architecture.repositoryflow

interface UserApi {
    // Simplified for the Lab (Removed Retrofit annotations to avoid classpath errors)
    suspend fun fetchUserById(id: String): User
}
