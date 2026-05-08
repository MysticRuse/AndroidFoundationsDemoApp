package com.sample.android.composebasics

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sample.android.composebasics.architecture.ArchitectureScreen
import com.sample.android.composebasics.lifecycleandandroidcomponents.ComponentsNLifeCycleScreen
import com.sample.android.composebasics.livecodingsamples.CodingExercisesScreen
import com.sample.android.composebasics.networkingdatapersistence.NetworkingScreen
import com.sample.android.composebasics.ui.CommonTopAppBar
import com.sample.android.composebasics.ui.Screen
import com.sample.android.composebasics.ui.theme.ComposeBasicsTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeBasicsTheme {

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    topBar = {
                        val title = when (currentRoute) {
                            Screen.BasicsCodeLab.route -> Screen.BasicsCodeLab.title
                            Screen.ViewModelExperiment.route -> Screen.ViewModelExperiment.title
                            Screen.ComposeExperiments.route -> Screen.ComposeExperiments.title
                            Screen.Architecture.route -> Screen.Architecture.title
                            Screen.ComponentsNLifeCycle.route -> Screen.ComponentsNLifeCycle.title
                            Screen.Networking.route -> Screen.Networking.title
                            Screen.CodingExercises.route -> Screen.CodingExercises.title
                            else -> Screen.Home.title
                        }

                        CommonTopAppBar(
                            title = title,
                            onBackClick = if (currentRoute != Screen.Home.route) {
                                { navController.popBackStack() }
                            } else null
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            LauncherScreen(onNavigate = { route -> navController.navigate(route) })
                        }
                        composable(Screen.BasicsCodeLab.route) { BasicsCodeLabScreen() }
                        composable(Screen.ViewModelExperiment.route) { ViewModelExperimentScreen() }
                        composable(Screen.ComposeExperiments.route) { ComposeExperimentsScreen() }
                        composable(Screen.Architecture.route) { ArchitectureScreen() }
                        composable(Screen.ComponentsNLifeCycle.route) { ComponentsNLifeCycleScreen() }
                        composable(Screen.Networking.route) { NetworkingScreen() }
                        composable(Screen.CodingExercises.route) { CodingExercisesScreen() }
                    }
                }
            }
        }
    }
}

@Composable
fun LauncherScreen(onNavigate: (String) -> Unit) {
    val items = listOf(
        ScreenLink(Screen.BasicsCodeLab.title, Screen.BasicsCodeLab.route),
        ScreenLink(Screen.ViewModelExperiment.title, Screen.ViewModelExperiment.route),
        ScreenLink(Screen.ComposeExperiments.title, Screen.ComposeExperiments.route),
        ScreenLink(Screen.Architecture.title, Screen.Architecture.route),
        ScreenLink(Screen.ComponentsNLifeCycle.title, Screen.ComponentsNLifeCycle.route),
        ScreenLink(Screen.Networking.title, Screen.Networking.route),
        ScreenLink(Screen.CodingExercises.title, Screen.CodingExercises.route),
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            items(items) { item ->
                ScreenLinkItem(screenLink = item) {
                    onNavigate(item.route)
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ScreenLinkItem(screenLink: ScreenLink, onClick: () -> Unit) {
    Text(
        text = screenLink.name,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp)
    )
}


data class ScreenLink(
    val name: String,
    val route: String
)

@Preview(showBackground = true)
@Composable
fun LauncherScreenPreview() {
    ComposeBasicsTheme {
        LauncherScreen(onNavigate = {})
    }
}
