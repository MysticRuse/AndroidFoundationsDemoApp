package com.sample.android.composebasics

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sample.android.composebasics.architecture.ArchitectureActivity
import com.sample.android.composebasics.lifecycleandandroidcomponents.AndroidComponentsNLifeCycleActivity
import com.sample.android.composebasics.networkingdatapersistence.NetworkingDataPersistenceActivity
import com.sample.android.composebasics.ui.CommonTopAppBar
import com.sample.android.composebasics.ui.theme.ComposeBasicsTheme

class HomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeBasicsTheme {
                Scaffold(
                    topBar = {
                        CommonTopAppBar(title = "Compose Learning")
                    }
                ) { innerPadding ->
                    LauncherScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LauncherScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activities = listOf(
        ActivityLink("Compose Basics Codelab", BasicsCodelabActivity::class.java),
        ActivityLink("ViewModel & Coroutines Experiment", ViewModelExperimentActivity::class.java),
        ActivityLink("Compose Learning Lab (Mini-Challenges)", ComposeExperimentsActivity::class.java),
        ActivityLink("Architecture and Design Patterns", ArchitectureActivity::class.java),
        ActivityLink("Lifecycle & Components", AndroidComponentsNLifeCycleActivity::class.java),
        ActivityLink("Networking & Data Persistence", NetworkingDataPersistenceActivity::class.java),
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            items(activities) { activityLink ->
                ActivityLinkItem(activityLink = activityLink) {
                    // Launch the activity when the item is clicked
                    val intent = Intent(context, activityLink.activityClass)
                    context.startActivity(intent)
                }
                HorizontalDivider() // Add a divider between items
            }
        }
    }
}

@Composable
fun ActivityLinkItem(activityLink: ActivityLink, onClick: () -> Unit) {
    Text(
        text = activityLink.name,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp)
    )
}

data class ActivityLink(
    val name: String,
    val activityClass: Class<*>
)

@Preview(showBackground = true)
@Composable
fun LauncherScreenPreview() {
    ComposeBasicsTheme {
        LauncherScreen()
    }
}
