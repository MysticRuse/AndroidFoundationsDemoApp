package com.sample.android.essentialcompose.architecture.uievents1time

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
}
