package com.sample.android.composebasics.architecture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sample.android.composebasics.architecture.offlinefirst.FakeTaskApi
import com.sample.android.composebasics.architecture.offlinefirst.FakeTaskDao
import com.sample.android.composebasics.architecture.offlinefirst.TaskRepository
import com.sample.android.composebasics.architecture.offlinefirst.TaskScreen
import com.sample.android.composebasics.architecture.offlinefirst.TaskViewModel
import com.sample.android.composebasics.architecture.repositoryflow.FakeUserApi
import com.sample.android.composebasics.architecture.repositoryflow.FakeUserDao
import com.sample.android.composebasics.architecture.repositoryflow.UserRepository
import com.sample.android.composebasics.architecture.repositoryflow.UserScreen
import com.sample.android.composebasics.architecture.repositoryflow.UserViewModel
import com.sample.android.composebasics.architecture.todo.TodoScreen
import com.sample.android.composebasics.architecture.uievents1time.EventsScreen
import com.sample.android.composebasics.ui.CommonTopAppBar
import com.sample.android.composebasics.ui.theme.ComposeBasicsTheme


enum class ArchitectureLesson(val title: String) {
    TODO("Todo (State Management)"),
    REPOSITORY_FLOW("Repository Flow (Data Layers)"),
    UI_EVENTS("UI Events (One-time logic)"),
    OFFLINE_FIRST("Offline-First Pattern")
}
@Composable
fun ArchitectureScreen() {
    var currentLesson by rememberSaveable { mutableStateOf<ArchitectureLesson?>(null) }


    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentLesson) {
            null -> LessonMenu { currentLesson = it }
            ArchitectureLesson.TODO -> TodoScreen()
            ArchitectureLesson.REPOSITORY_FLOW -> {
                val viewModel: UserViewModel = remember {
                    val api = FakeUserApi()
                    val dao = FakeUserDao()
                    val repo = UserRepository(api, dao)
                    UserViewModel(repo)
                }
                UserScreen(viewModel)
            }
            ArchitectureLesson.UI_EVENTS -> EventsScreen()
            ArchitectureLesson.OFFLINE_FIRST -> {
                val viewModel: TaskViewModel = remember {
                    val api = FakeTaskApi()
                    val dao = FakeTaskDao()
                    val repo = TaskRepository(api, dao)
                    TaskViewModel(repo)
                }
                TaskScreen(viewModel)
            }
        }
    }
}

@Composable
fun LessonMenu(onSelect: (ArchitectureLesson) -> Unit) {
    val lessons = listOf(
        ArchitectureLesson.TODO,
        ArchitectureLesson.REPOSITORY_FLOW,
        ArchitectureLesson.UI_EVENTS,
        ArchitectureLesson.OFFLINE_FIRST
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(lessons) { lesson ->
            ListItem(
                headlineContent = { Text(lesson.title) },
                modifier = Modifier.clickable { onSelect(lesson) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun ArchitecturePlaceholder(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$name Screen - Ready for your hand-coded implementation")
    }
}

class ArchitectureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeBasicsTheme {
                var currentLesson by rememberSaveable { mutableStateOf<ArchitectureLesson?>(null) }

                Scaffold(
                    topBar = {
                        CommonTopAppBar(
                            title = currentLesson?.title ?: "Architecture Lab",
                            onBackClick = {
                                if (currentLesson == null) finish() else currentLesson = null
                            }
                        )
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentLesson) {
                            null -> LessonMenu { currentLesson = it }
                            ArchitectureLesson.TODO -> TodoScreen()
                            ArchitectureLesson.REPOSITORY_FLOW -> {
                                val viewModel: UserViewModel = remember {
                                    val api = FakeUserApi()
                                    val dao = FakeUserDao()
                                    val repo = UserRepository(api, dao)
                                    UserViewModel(repo)
                                }
                                UserScreen(viewModel)
                            }
                            ArchitectureLesson.UI_EVENTS -> EventsScreen()
                            ArchitectureLesson.OFFLINE_FIRST -> {
                                val viewModel: TaskViewModel = remember {
                                    val api = FakeTaskApi()
                                    val dao = FakeTaskDao()
                                    val repo = TaskRepository(api, dao)
                                    TaskViewModel(repo)
                                }
                                TaskScreen(viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
