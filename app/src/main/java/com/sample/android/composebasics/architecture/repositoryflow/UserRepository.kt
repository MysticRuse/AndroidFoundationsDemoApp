package com.sample.android.composebasics.architecture.repositoryflow

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.concurrent.ConcurrentHashMap

/**
 * 1.First Launch: You should see:
 *      ◦Repository: DB Miss. Fetching from Network.
 *      ◦Repository: Fetching user 1 from Network...
 *      ◦Repository: Network success. Saved to DB and Cache.
 * 2.Navigate away and back: You should see:
 *      ◦Repository: Returning from Memory Cache (Instant)
 * 3.Click "Refresh" button: You should see:
 *      ◦Repository: Fetching user 1 from Network... (Force update)
 */
class UserRepository(
    private val userApi: UserApi,
    private val userDao: UserDao
) {
    private val TAG = "UserRepository"

    // 1. In-memory cache - ConcurrentHashMap: Thread-safe without locking the whole map on reads
    private val userCache= ConcurrentHashMap<String, CacheEntry>()

    data class CacheEntry(
        val user: User,
        val timestamp: Long = System.currentTimeMillis()) {
        fun isExpired(ttlMs: Long = CACHE_TTL_MS): Boolean {
            return System.currentTimeMillis() - timestamp > ttlMs
        }
        companion object {
            const val CACHE_TTL_MS = 5 * 60 * 1000L // 5 minutes
        }
    }

    fun getUserById(id: String): Flow<User> = flow {
        // Step 1: Check in-memory cache
        userCache[id]?.takeIf { !it.isExpired() }?.let {
            Log.d(TAG, "Repository: Returning from Memory Cache")
            emit(it.user)
            return@flow                         // Cache hit → done
        }

        // Step 2: fetch from local DB
        val dbUser = userDao.getUserById(id)
        if (dbUser != null) {
            Log.d(TAG, "Repository: Cache Miss. Found in DB. Updating Cache.")
            // Update cache and emit
            userCache[id] = CacheEntry(dbUser)
            emit(dbUser)
        } else {
            Log.d(TAG, "Repository: DB Miss. Fetching from Network.")
            // Step 3: fetch from network API
            fetchAndStore(id)
            // Emit the newly fetched user from cache/DB
            userCache[id]?.let { emit(it.user) }
        }
    }.catch { e -> // Surface network/DB errors without killing the flow;
        // upstream collectors decide how to handle them.
        throw Exception("Failed to load user $id", e) // Could be a RepositoryException definition.
    }
    suspend fun refreshUser(id: String) {
        userCache.remove(id) // Invalidate cache first
        fetchAndStore(id)  // Throws on failure
    }

    private suspend fun fetchAndStore(id: String) {
        try {
            Log.d(TAG, "Repository: Fetching user $id from Network...")
            val networkUser = userApi.fetchUserById(id)

            // Step 4: Store results in both cache and database
            userDao.insertUser(networkUser)
            userCache[id] = CacheEntry(networkUser)
            Log.d(TAG, "Repository: Network success. Saved to DB and Cache.")
        } catch (e: Exception) {
            Log.d(TAG, "Repository: Network Error: ${e.message}")
            throw e
        }
    }
}