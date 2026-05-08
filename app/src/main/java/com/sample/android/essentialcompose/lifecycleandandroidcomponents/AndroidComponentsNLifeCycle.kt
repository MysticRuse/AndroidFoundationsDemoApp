package com.sample.android.essentialcompose.lifecycleandandroidcomponents

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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sample.android.essentialcompose.lifecycleandandroidcomponents.connectivityreceiver.ConnectivityScreen
import com.sample.android.essentialcompose.lifecycleandandroidcomponents.lifecycleawarelocationtrack.LocationTrackScreen
import com.sample.android.essentialcompose.lifecycleandandroidcomponents.savedstatehandle.SearchRepository
import com.sample.android.essentialcompose.lifecycleandandroidcomponents.savedstatehandle.SearchScreen
import com.sample.android.essentialcompose.lifecycleandandroidcomponents.savedstatehandle.SearchViewModel
import com.sample.android.essentialcompose.ui.CommonTopAppBar
import com.sample.android.essentialcompose.ui.theme.ComposeBasicsTheme

@Composable
fun ComponentsNLifeCycleScreen() {
    var currentLesson by rememberSaveable { mutableStateOf("Menu") }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentLesson) {
            "Menu" -> ComponentsLessonMenu { currentLesson = it }
            "SavedStateHandle (Process Death)" -> {
                val repository = remember { SearchRepository() }
                val viewModel: SearchViewModel = viewModel(
                    factory = SearchViewModelFactory(repository)
                )
                SearchScreen(viewModel = viewModel)
            }
            "Lifecycle-Aware Location" -> {
                LocationTrackScreen()
            }
            "Connectivity Monitor (Broadcast/Callback)" -> {
                ConnectivityScreen()
            }
        }
    }
}

@Composable
fun ComponentsLessonMenu(onSelect: (String) -> Unit) {
    val lessons = listOf(
        "SavedStateHandle (Process Death)",
        "Lifecycle-Aware Location",
        "Connectivity Monitor (Broadcast/Callback)"
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

class SearchViewModelFactory(private val repository: SearchRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
        return SearchViewModel(savedStateHandle, repository) as T
    }
}

class AndroidComponentsNLifeCycleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeBasicsTheme {
                var currentLesson by rememberSaveable { mutableStateOf("Menu") }

                Scaffold(
                    topBar = {
                        CommonTopAppBar(
                            title = if (currentLesson == "Menu") "Lifecycle & Components" else currentLesson,
                            onBackClick = {
                                if (currentLesson == "Menu") finish() else currentLesson = "Menu"
                            }
                        )
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentLesson) {
                            "Menu" -> ComponentsLessonMenu { currentLesson = it }
                            "SavedStateHandle (Process Death)" -> {
                                val repository = remember { SearchRepository() }
                                val viewModel: SearchViewModel = viewModel(
                                    factory = SearchViewModelFactory(repository)
                                )
                                SearchScreen(viewModel = viewModel)
                            }
                            "Lifecycle-Aware Location" -> {
                                LocationTrackScreen()
                            }
                            "Connectivity Monitor (Broadcast/Callback)" -> {
                                ConnectivityScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
