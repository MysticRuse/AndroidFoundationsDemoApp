package com.sample.android.composebasics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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


@Composable
fun ComposeExperimentsScreen() {
    var currentExperiment by remember { mutableStateOf("Menu") }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (currentExperiment) {
            "Menu" -> ExperimentMenu { currentExperiment = it }
            "Counter" -> CounterExperiment()
            "Searchable List" -> SearchableListExperiment()
            "Toggle Button" -> ToggleButtonExperiment()
            "Login Form" -> LoginFormExperiment()
            "Data Fetching" -> DataFetchingExperiment()
        }
    }
}

class ComposeExperimentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeBasicsTheme {
                var currentExperiment by remember { mutableStateOf("Menu") }

                Scaffold(
                    topBar = {
                        CommonTopAppBar(
                            title = if (currentExperiment == "Menu") "Compose Learning Lab" else currentExperiment,
                            onBackClick = {
                                if (currentExperiment == "Menu") finish() else currentExperiment = "Menu"
                            }
                        )
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentExperiment) {
                            "Menu" -> ExperimentMenu { currentExperiment = it }
                            "Counter" -> CounterExperiment()
                            "Searchable List" -> SearchableListExperiment()
                            "Toggle Button" -> ToggleButtonExperiment()
                            "Login Form" -> LoginFormExperiment()
                            "Data Fetching" -> DataFetchingExperiment()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExperimentMenu(onSelect: (String) -> Unit) {
    val experiments = listOf(
        "Counter",
        "Searchable List",
        "Toggle Button",
        "Login Form",
        "Data Fetching"
    )
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(experiments) { experiment ->
            ListItem(
                headlineContent = { Text(experiment) },
                modifier = Modifier.clickable { onSelect(experiment) }
            )
            HorizontalDivider()
        }
    }
}

/**
 * Build a Counter composable with + and - buttons.
 * The count should never go below 0. Display the current count between the buttons.
 */
@Composable
fun CounterExperiment() {
    // Numeric state with using remember {mutableIntStateOf(int)}
    var count by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Count: $count", style = MaterialTheme.typography.displayMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { if (count > 0) count--}) { Text("-", style = MaterialTheme.typography.displaySmall) }
            Button(onClick = { count++} ) {Text("+", style = MaterialTheme.typography.displaySmall)}
        }
    }
}

@Composable
fun SearchableListExperiment() {
    var query by remember { mutableStateOf("") }
    val items = listOf("Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig", "Grape")
    val filteredItems = items.filter { it.contains(query, ignoreCase = true) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search Fruits") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(filteredItems) { fruit ->
                Text(fruit, modifier = Modifier.padding(vertical = 8.dp))
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ToggleButtonExperiment() {
    var isOn by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (isOn) "Using Switch: The switch is ON" else "Using Switch: The switch is OFF")
        Switch(checked = isOn, onCheckedChange = { isOn = it })

        Text(
            text = if (isOn) "Using Animated Color: ON" else "Using Animated Color: OFF",
            modifier = Modifier.padding(top = 16.dp)
        )
        ToggleButton(isOn = isOn, onToggle = { isOn = !isOn })
    }
}

@Composable
fun ToggleButton(isOn: Boolean, onToggle: () -> Unit) {
    // 1. Define animated color
    val bgColor by animateColorAsState(
        targetValue = if (isOn) Color.Green else Color.Gray,
        label = "bgColor Animation")
    // 2. Button UI
    Button(
        onClick = onToggle,
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = if (isOn) "ON" else "OFF")
    }
}

@Composable
fun LoginFormExperiment() {
    var loginStatus by remember { mutableStateOf("Please Login") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(loginStatus, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        LoginForm { email, password ->
            loginStatus = "Login Successful!"
        }
    }
}

@Composable
fun LoginForm(onSubmit: (email: String, password: String)-> Unit) {
    // Local State
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val emailValid = email.contains("@") && email.endsWith(".com")
    val passwordValid = password.length >= 8

    // 2. Layout and UI
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        if (!emailValid && email.isNotEmpty()) Text("Invalid email", color = Color.Red)

        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        if (!passwordValid && password.isNotEmpty()) Text("Min 8 characters", color = Color.Red)

        Button(
            onClick = { onSubmit(email, password) },
            modifier = Modifier.align(Alignment.End),
            enabled = emailValid && passwordValid
        ) {
            Text("Login")
        }
    }
}


sealed interface UserUiState {
    object Loading : UserUiState
    data class Error(val message: String) : UserUiState
    data class Success(val data: String) : UserUiState
}

class UserViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val uiState: StateFlow<UserUiState> = _uiState

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading
            try {
                delay(2000)
                _uiState.value = UserUiState.Success("Welcome, $userId!")
            } catch (e: Exception) {
                _uiState.value = UserUiState.Error("Failed to load user: $userId - ${e.message}")
            }
        }
    }
}

@Composable
fun UserProfile(userId: String, viewModel: UserViewModel) {
    // Observe StateFlow as Compose State
    val uiState by viewModel.uiState.collectAsState()

    // Trigger data load when composable first appears or userId changes.
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is UserUiState.Loading -> CircularProgressIndicator()
            is UserUiState.Success -> Text((uiState as UserUiState.Success).data, style = MaterialTheme.typography.headlineMedium)
            is UserUiState.Error -> {
                Text((uiState as UserUiState.Error).message, color = Color.Red, style = MaterialTheme.typography.headlineMedium)
                Button(onClick = { viewModel.loadUser(userId) }) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun DataFetchingExperiment() {
    val viewModel: UserViewModel = viewModel()
    // Call the profile composable
    UserProfile("ComposeLearner_01", viewModel)
}
