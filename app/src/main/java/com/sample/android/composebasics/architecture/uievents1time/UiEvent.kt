package com.sample.android.composebasics.architecture.uievents1time

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
}
