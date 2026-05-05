package com.sample.android.composebasics.architecture.uievents1time

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EventsViewModel : ViewModel() {

    // Using a Channel for one-time events. 
    // Buffered to ensure events aren't lost if the collector isn't ready for a split second,
    // but they are consumed only once.
    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun triggerSnackbar() {
        viewModelScope.launch {
            _events.send(UiEvent.ShowSnackbar("This is a one-time snackbar!"))
        }
    }

    fun triggerNavigation() {
        viewModelScope.launch {
            _events.send(UiEvent.Navigate("details_screen"))
        }
    }
}
