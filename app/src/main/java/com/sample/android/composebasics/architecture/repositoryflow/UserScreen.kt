package com.sample.android.composebasics.architecture.repositoryflow

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun UserScreen(viewModel: UserViewModel) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (user != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("User ID: ${user?.id}", style = MaterialTheme.typography.labelLarge)
                    Text("Name: ${user?.name}", style = MaterialTheme.typography.headlineSmall)
                    Text("Email: ${user?.email}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            Text("No user data loaded yet.")
            if (isRefreshing) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }

        Button(
            onClick = { viewModel.refresh() },
            enabled = !isRefreshing
        ) {
            if (isRefreshing) {
                Text("Fetching from Network...")
            } else {
                Text("Refresh (Force Network Fetch)")
            }
        }

        Text(
            text = "Check Logcat to see the Cache -> DB -> API flow",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
