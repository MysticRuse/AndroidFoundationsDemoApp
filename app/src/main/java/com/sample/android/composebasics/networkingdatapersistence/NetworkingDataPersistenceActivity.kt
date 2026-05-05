package com.sample.android.composebasics.networkingdatapersistence

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sample.android.composebasics.ui.CommonTopAppBar
import com.sample.android.composebasics.ui.theme.ComposeBasicsTheme

/**
 * Main Activity for Networking and Data Persistence Exercises.
 * This activity provides a menu to explore the implementations of Room, Retrofit, OkHttp Interceptors,
 * and the NetworkBoundResource pattern.
 */
class NetworkingDataPersistenceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeBasicsTheme {
                var currentLesson by rememberSaveable { mutableStateOf("Menu") }

                Scaffold(
                    topBar = {
                        CommonTopAppBar(
                            title = if (currentLesson == "Menu") "Networking & Persistence" else currentLesson,
                            onBackClick = {
                                if (currentLesson == "Menu") finish() else currentLesson = "Menu"
                            }
                        )
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentLesson) {
                            "Menu" -> PersistenceLessonMenu { currentLesson = it }
                            "Room Database (Notes App)" -> RoomLessonScreen()
                            "OkHttp Auth Interceptor" -> InterceptorLessonScreen()
                            "NetworkBoundResource Pattern" -> NBRScreen()
                            "Retrofit Blog API" -> RetrofitLessonScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersistenceLessonMenu(onSelect: (String) -> Unit) {
    val lessons = listOf(
        "Room Database (Notes App)",
        "OkHttp Auth Interceptor",
        "NetworkBoundResource Pattern",
        "Retrofit Blog API"
    )
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(lessons) { lesson ->
            ListItem(
                headlineContent = { Text(lesson) },
                modifier = Modifier.clickable { onSelect(lesson) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun RoomLessonScreen() {
    Text(
        text = "Room implementation files created in networkingdatapersistence.room package.\n\n" +
                "Check Note.kt, Folder.kt, NoteDao.kt, and AppDatabase.kt for the implementation of entities, " +
                "JOIN queries, and DAO methods for the note-taking app exercise.",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun InterceptorLessonScreen() {
    Text(
        text = "OkHttp Interceptor implementation created in networkingdatapersistence.okhttp package.\n\n" +
                "Check AuthInterceptor.kt for thread-safe token refresh and 401 retry logic using synchronized blocks.",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun NBRScreen() {
    Text(
        text = "NetworkBoundResource implementation created in networkingdatapersistence.util package.\n\n" +
                "Check NetworkBoundResource.kt for the Flow-based implementation that manages caching logic, " +
                "network fetching, and error handling as per Exercise 87.",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun RetrofitLessonScreen() {
    Text(
        text = "Retrofit Blog API implementation created in networkingdatapersistence.retrofit package.\n\n" +
                "Check BlogApi.kt for the interface with GET, POST, PUT, and DELETE endpoints as required for Exercise 84.",
        modifier = Modifier.padding(16.dp)
    )
}
