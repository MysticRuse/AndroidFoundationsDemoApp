package com.sample.android.essentialcompose.lifecycleandandroidcomponents.connectivityreceiver

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

/**
 * A reactive monitor for network connectivity using callbackFlow.
 * It bridges the imperative ConnectivityManager.NetworkCallback to a StateFlow.
 */
class ConnectivityMonitor(context: Context) {

    private val connectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * A StateFlow that emits true when the device is online and false otherwise.
     * Uses stateIn with WhileSubscribed(5000) to handle screen rotations efficiently.
     */
    @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
    val isOnline: StateFlow<Boolean> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d("ConnectivityMonitor", "Callback: Network Available")
                trySend(true)
            }

            override fun onLost(network: Network) {
                Log.d("ConnectivityMonitor", "Callback: Network Lost")
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                Log.d("ConnectivityMonitor", "Callback: Capabilities Changed (hasInternet=$hasInternet)")
                trySend(hasInternet)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        Log.d("ConnectivityMonitor", "Registering NetworkCallback via callbackFlow...")
        connectivityManager.registerNetworkCallback(request, networkCallback)

        // Initial check to emit state immediately upon collection
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val initialStatus = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        trySend(initialStatus)

        // awaitClose is called when the flow collection is stopped
        awaitClose {
            Log.d("ConnectivityMonitor", "Unregistering NetworkCallback (Flow closed)")
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
    .distinctUntilChanged()
    .stateIn(
        scope = GlobalScope, // Using GlobalScope for lab simplicity; use ViewModelScope in production
        started = SharingStarted.WhileSubscribed(5000), // surviving rotation gap
        initialValue = false
    )
    // ensure that the ConnectivityMonitor instance is held in a ViewModel (or a singleton), so the object itself survives the rotation.
}
