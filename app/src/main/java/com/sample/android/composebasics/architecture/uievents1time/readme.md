Design and implement a UiEvent system for one-time events (snackbars, navigation). ViewModel exposes events, UI consumes them exactly once.

// Hint: Use Channel or SharedFlow with replay=0
// Events should not be re-delivered on configuration change

```sealed class UiEvent {
data class ShowSnackbar(val message: String) : UiEvent()
data class Navigate(val route: String) : UiEvent()
}

class MyViewModel : ViewModel() {
// Implement event emission
}

// In Composable:
// Implement event collection
