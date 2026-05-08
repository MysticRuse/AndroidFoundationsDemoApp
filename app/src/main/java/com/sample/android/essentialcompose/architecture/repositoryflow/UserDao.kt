package com.sample.android.essentialcompose.architecture.repositoryflow

interface UserDao {
    // Simplified for the Lab (Removed Room annotations to avoid classpath errors)
    fun getUserById(id: String): User?
    fun insertUser(user: User)
}
