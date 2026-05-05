package com.sample.android.composebasics.architecture.uievents1time

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Architecture/UI Events (One-time logic)
 * In the UI Events screen, trigger a snackbar and then rotate the device.
 * You'll notice the snackbar doesn't reappear (consumed once).
 */
@Composable
fun EventsScreen(
    viewModel: EventsViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.Navigate -> {
                    // In a real app, you would use a NavController here.
                    // For this lab, we'll just show a snackbar to simulate navigation.
                    snackbarHostState.showSnackbar("Navigating to: ${event.route}")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "One-Time Events Lab",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Button(
                onClick = { viewModel.triggerSnackbar() },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Trigger Snackbar")
            }
            Button(
                onClick = { viewModel.triggerNavigation() },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Trigger Navigation (Simulated)")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Notice how events survive configuration changes but are only handled once.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
