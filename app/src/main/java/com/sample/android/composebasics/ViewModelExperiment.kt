package com.sample.android.composebasics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sample.android.composebasics.ui.CommonTopAppBar
import com.sample.android.composebasics.ui.theme.ComposeBasicsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// 1. Corrected ViewModel
class MyViewModel : ViewModel() {
    private val _state = MutableStateFlow("Initial Data")
    val state: StateFlow<String> = _state

    fun refreshData() {
        // Correct way: use viewModelScope
        viewModelScope.launch {
            _state.value = "Loading..."
            delay(2000) // Simulate network call
            _state.value = "New Data from Repository!"
        }
    }
}

@Composable
fun ViewModelExperimentScreen() {
    val viewModel: MyViewModel = viewModel()
    val data by viewModel.state.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = data,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(onClick = { viewModel.refreshData() }) {
                Text("Refresh Data")
            }
        }
    }
}


//================== Old Activity ============================
class ViewModelExperimentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeBasicsTheme {
                Scaffold(
                    topBar = {
                        CommonTopAppBar(
                            title = "ViewModel Experiment",
                            onBackClick = { finish() }
                        )
                    }
                ) { innerPadding ->
                    val viewModel: MyViewModel = viewModel()
                    val data by viewModel.state.collectAsState()

                    Surface(
                        modifier = Modifier.padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = data,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Button(onClick = { viewModel.refreshData() }) {
                                Text("Refresh Data")
                            }
                        }
                    }
                }
            }
        }
    }
}
