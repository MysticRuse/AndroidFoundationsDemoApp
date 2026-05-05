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

class ArchitectureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeBasicsTheme {
                var currentLesson by rememberSaveable { mutableStateOf("Menu") }

                Scaffold(
                    topBar = {
                        CommonTopAppBar(
                            title = if (currentLesson == "Menu") "Architecture Lab" else currentLesson,
                            onBackClick = {
                                if (currentLesson == "Menu") finish() else currentLesson = "Menu"
                            }
                        )
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentLesson) {
                            "Menu" -> LessonMenu { currentLesson = it }
                            "Todo (State Management)" -> TodoScreen()
                            "Repository Flow (Data Layers)" -> {
                                val viewModel: UserViewModel = remember {
                                    val api = FakeUserApi()
                                    val dao = FakeUserDao()
                                    val repo = UserRepository(api, dao)
                                    UserViewModel(repo)
                                }
                                UserScreen(viewModel)
                            }
                            "UI Events (One-time logic)" -> EventsScreen()
                            "Offline-First Pattern" -> {
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

@Composable
fun LessonMenu(onSelect: (String) -> Unit) {
    val lessons = listOf(
        "Todo (State Management)",
        "Repository Flow (Data Layers)",
        "UI Events (One-time logic)",
        "Offline-First Pattern"
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
fun ArchitecturePlaceholder(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("$name Screen - Ready for your hand-coded implementation")
    }
}
