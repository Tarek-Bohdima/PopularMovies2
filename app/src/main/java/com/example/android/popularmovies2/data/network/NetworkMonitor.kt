/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Push-based connectivity signal.
 *
 * `isOnline` emits `true` whenever there's at least one network with both
 * [NetworkCapabilities.NET_CAPABILITY_INTERNET] and
 * [NetworkCapabilities.NET_CAPABILITY_VALIDATED], `false` otherwise. See the rationale
 * for the capability-pair check in the (now-removed) `ConnectionUtils` history — short
 * version: transport checks misfire on captive portals and VPNs.
 *
 * Collect from a `ViewModel` and surface as `StateFlow<Boolean>`; render reactively
 * instead of polling at user-action time.
 */
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}

class ConnectivityManagerNetworkMonitor(
    private val connectivityManager: ConnectivityManager?,
    private val networkRequest: NetworkRequest,
) : NetworkMonitor {

    // Production entrypoint. The `NetworkRequest.Builder` call is hoisted here (outside
    // `callbackFlow`) so unit tests can bypass the Android-framework `Builder` stubs by
    // calling the primary constructor with a mock `NetworkRequest`.
    constructor(context: Context) : this(
        connectivityManager = context.applicationContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager,
        networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build(),
    )

    override val isOnline: Flow<Boolean> = callbackFlow {
        val cm = connectivityManager
        if (cm == null) {
            trySend(false)
            close()
            return@callbackFlow
        }

        // Multiple networks (e.g. WiFi + cellular) can be active concurrently; we're
        // online if *any* of them is validated. Track the set and emit set-non-empty.
        val validatedNetworks = mutableSetOf<Network>()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                if (validatedInternet(caps)) {
                    validatedNetworks += network
                } else {
                    validatedNetworks -= network
                }
                trySend(validatedNetworks.isNotEmpty())
            }

            override fun onLost(network: Network) {
                validatedNetworks -= network
                trySend(validatedNetworks.isNotEmpty())
            }
        }

        cm.registerNetworkCallback(networkRequest, callback)

        // Seed: emit the current snapshot so collectors see the right value immediately,
        // not after the first callback fires.
        trySend(validatedInternet(cm.getNetworkCapabilities(cm.activeNetwork)))

        awaitClose { cm.unregisterNetworkCallback(callback) }
    }
        .conflate()
        .distinctUntilChanged()

    private fun validatedInternet(caps: NetworkCapabilities?): Boolean =
        caps != null &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
