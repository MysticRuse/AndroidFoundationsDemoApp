package com.sample.android.essentialcompose.livecodingsamples

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Problem 5: Paginated List with Loading Footer
 * Build a LazyColumn that loads 20 items at a time. When the user scrolls near the bottom (within 5 items), trigger loading the next page. Show a CircularProgressIndicator as the last item while loading. Handle the "no more pages" state.
 * Requirements:
 * Initial load of 20 items
 * Detect when user scrolls within 5 items of the bottom
 * Trigger next page load automatically
 * Show a loading spinner as the last list item while fetching
 * Handle end-of-data — stop requesting and hide the spinner
 * Prevent duplicate page fetches during fast scrolling
 * Interviewer follow-ups to consider:
 * How do you use derivedStateOf for the scroll detection?
 * What if a page request fails? How do you show a retry?
 */

/**
 * Mental Model to explain out loud:
 * Scroll event
 *     → layoutInfo changes every frame
 *     → derivedStateOf computes: am I within 5 of the end?
 *     → only when that flips true → LaunchedEffect fires → loadNextPage()
 *     → ViewModel guard: already loading? bail out
 *     → fetch runs → items appended → isLoading = false
 *     → if no more data → isEndReached = true → guard stops all future calls
 */

// ViewModel Layer
// 1. SSOT for List Screen
data class PaginatedListState(
    //val items: List<String> = emptyList(),
    val items: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isEndReached: Boolean = false,
    val currentPage: Int = 0,

    // 9. Add error field in state
    val error: String? = null
)

// 2. ViewModel
@HiltViewModel
class PaginatedListViewModel @Inject constructor(
    private val repository: PostRepository
): ViewModel() {
    private val _state = MutableStateFlow(PaginatedListState())
    val state: StateFlow<PaginatedListState> = _state.asStateFlow()

    private val pageSize = 20
    private val totalFakeItems = 65

    init  {
        loadNextPage()
    }

    fun loadNextPage() {

        val current  = _state.value
        // Guard: Duplicate fetch prevention
        if (current.isLoading || current.isEndReached) return

        viewModelScope.launch {

            _state.update { it.copy(isLoading = true, error = null) }
            repository.getPosts(current.currentPage, pageSize)
                .onSuccess { newItems ->
                    _state.update {
                        it.copy(
                            items = it.items + newItems,
                            currentPage = it.currentPage + 1,
                            isLoading = false,
                            isEndReached = newItems.size < pageSize,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
//            try {
//                val start = current.currentPage * pageSize
//                val end = minOf(start + pageSize, totalFakeItems)
//                val newItems = (start until end).map { "Item #$it" }
//
//                _state.update {
//                    it.copy(
//                        items = it.items + newItems,
//                        currentPage = it.currentPage + 1,
//                        isLoading = false,
//                        isEndReached = end >= totalFakeItems,
//                    )
//                }
//            } catch (e: Exception) {
//                _state.update { it.copy(isLoading = false, error = e.message) }
//            }
        }
    }
}


// Presentation Layer
// 3. The Composable Shell
@Composable
fun PaginatedListScreen(viewModel: PaginatedListViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PaginatedList (
        state = state,
        onLoadMore = { viewModel.loadNextPage() }
    )
}

@Composable
fun PaginatedList(
    state: PaginatedListState,
    onLoadMore: () -> Unit
) {
    // 4. derivedStateOf Scroll Detection

    /**
     * Why derivedStateOf?
     * listState.layoutInfo changes on every scroll frame. Without derivedStateOf,
     * LaunchedEffect key would recompose constantly. derivedStateOf caches the boolean and only
     * notifies observers when true/false actually flips — so LaunchedEffect fires at most once
     * per crossing of the threshold.
     */
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleIndex >= totalItems - 5 // Within 5 items from the end
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !state.isEndReached) onLoadMore()
    }

    // 5. LazyColumn
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(
            items = state.items,
            key = { it.id } // stable keys prevent recomposition artifacts
        ) { item ->
            ItemRow(item = item)
        }

        // 6. Loading Footer
        if (state.isLoading) {
            item(key = "loading_footer") {
                LoadingFooter()
            }
        }

        // 7. End of list message
        if (state.isEndReached && !state.isLoading) {
            item(key = "end_footer") {
                EndFooter()
            }
        }

        // 10. Add a retry footer
        if (state.error != null) {
            item(key = "error_footer") {
                Text(
                    text = state.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLoadMore() }
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EndFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "You've reached the end",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LoadingFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            strokeWidth = 2.dp
        )
    }
}

// 8. ItemRow Composable
@Composable
fun ItemRow(item: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = item.title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// Repository Layer

// Add the repository too instead of hardcoded items
data class Post(
    val id: Int,
    val title: String,
    val body:String
)

// Remote Data Source (Retrofit)
interface PostApiService {
    @GET("posts")
    suspend fun getPosts(
        @Query("_page") page: Int,
        @Query("_limit") limit: Int = 20
    ): List<PostDto>
}

data class PostDto(val id: Int, val title: String, val body: String)

// Mapper - keep API model separate from domain model
fun PostDto.toDomain() = Post(id = id, title = title, body = body)

// Repository returns Result<T> — the ViewModel never sees exceptions directly.
// The repository is also the right place to add a local cache (Room) later without touching the ViewModel.
interface PostRepository {
    suspend fun getPosts(page:Int, pageSize: Int): Result<List<Post>>
}

class PostRepositoryImpl(
    private val apiService: PostApiService
): PostRepository {
    override suspend fun getPosts(page: Int, pageSize: Int): Result<List<Post>> {
        return try {
            val posts = apiService.getPosts(page = page, limit = pageSize).map { it.toDomain() }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Dependency Injection
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): PostApiService =
        retrofit.create(PostApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePostRepository(api: PostApiService): PostRepository =
        PostRepositoryImpl(api)
}
