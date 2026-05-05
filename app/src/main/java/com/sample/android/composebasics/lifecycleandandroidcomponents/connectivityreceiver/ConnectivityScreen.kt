package com.sample.android.composebasics.lifecycleandandroidcomponents.connectivityreceiver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ConnectivityScreen() {
    val context = LocalContext.current
    
    // Create the monitor. In a real app, this would likely live in a ViewModel.
    val connectivityMonitor = remember { ConnectivityMonitor(context) }
    
    // collectAsStateWithLifecycle automatically registers the callbackFlow when the 
    // lifecycle is STARTED and unregisters it when it's STOPPED.
    val isOnline by connectivityMonitor.isOnline.collectAsStateWithLifecycle(initialValue = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Connectivity Monitor Lab",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        val statusColor = if (isOnline) Color(0xFF4CAF50) else Color(0xFFF44336)
        val statusText = if (isOnline) "Online" else "Offline"
        val statusIcon = if (isOnline) Icons.Default.CheckCircle else Icons.Default.Warning

        Box(
            modifier = Modifier
                .size(150.dp)
                .background(statusColor.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = statusColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.headlineSmall,
                    color = statusColor
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Toggle Airplane Mode or Wi-Fi.",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Text(
            text = "Implemented using callbackFlow + collectAsStateWithLifecycle",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
