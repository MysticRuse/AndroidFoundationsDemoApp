package com.sample.android.essentialcompose.testing

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun CounterScreen() {
    var count by remember { mutableStateOf(0) }
    Column {
        Text(
            text = "Count: $count",
            modifier = Modifier.testTag("count_text")
        )
        Button(
            onClick = { count++ },
            modifier = Modifier.testTag("increment_btn")
        ) {
            Text("+")
        }
        Button(
            onClick = { count = 0 },
            modifier = Modifier.testTag("reset_btn")
        ) {
            Text("Reset")
        }
    }
}

