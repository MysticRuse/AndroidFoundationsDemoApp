package com.sample.android.essentialcompose.networkingdatapersistence.util

import kotlinx.coroutines.flow.*

/**
 * A generic function that implements the Network Bound Resource pattern.
 * It manages the logic of emitting cached data, fetching fresh data, and updating the cache.
 */
fun <T> networkBoundResource(
    query: () -> Flow<T>,
    fetch: suspend () -> T,
    saveFetchResult: suspend (T) -> Unit,
    shouldFetch: (T) -> Boolean = { true }
): Flow<Resource<T>> = flow {
    // 1. Emit loading state with current cached data
    val data = query().first()
    emit(Resource.Loading(data))

    if (shouldFetch(data)) {
        try {
            // 2. Fetch fresh data from network
            val fetchResult = fetch()
            
            // 3. Save fresh data to local database
            saveFetchResult(fetchResult)
            
            // 4. Emit success with updated local data
            emitAll(query().map { Resource.Success(it) })
        } catch (throwable: Throwable) {
            // 5. Handle errors and emit error state with stale data
            emitAll(query().map { Resource.Error(throwable, it) })
        }
    } else {
        // 6. If no fetch is needed, just emit success with current data
        emitAll(query().map { Resource.Success(it) })
    }
}
