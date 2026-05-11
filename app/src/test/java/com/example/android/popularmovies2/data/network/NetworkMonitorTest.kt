package com.example.android.popularmovies2.data.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.android.popularmovies2.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkMonitorTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val connectivityManager: ConnectivityManager = mock()
    private val request: NetworkRequest = mock()

    private fun monitor(cm: ConnectivityManager? = connectivityManager) =
        ConnectivityManagerNetworkMonitor(cm, request)

    @Test
    fun seed_isFalseWhenConnectivityServiceMissing() = runTest {
        assertEquals(false, monitor(cm = null).isOnline.first())
    }

    @Test
    fun seed_isFalseWhenNoActiveNetwork() = runTest(UnconfinedTestDispatcher()) {
        val emissions = mutableListOf<Boolean>()
        val job = launch { monitor().isOnline.toList(emissions) }
        job.cancel()
        assertEquals(listOf(false), emissions)
    }

    @Test
    fun seed_isFalseWhenActiveNetworkIsInternetButNotValidated() = runTest(UnconfinedTestDispatcher()) {
        val network: Network = mock()
        val caps = internetOnlyCaps()
        whenever(connectivityManager.activeNetwork).thenReturn(network)
        whenever(connectivityManager.getNetworkCapabilities(network)).thenReturn(caps)
        val emissions = mutableListOf<Boolean>()
        val job = launch { monitor().isOnline.toList(emissions) }
        job.cancel()
        assertEquals(listOf(false), emissions)
    }

    @Test
    fun seed_isTrueWhenActiveNetworkIsValidated() = runTest(UnconfinedTestDispatcher()) {
        val network: Network = mock()
        val caps = validatedCaps()
        whenever(connectivityManager.activeNetwork).thenReturn(network)
        whenever(connectivityManager.getNetworkCapabilities(network)).thenReturn(caps)
        val emissions = mutableListOf<Boolean>()
        val job = launch { monitor().isOnline.toList(emissions) }
        job.cancel()
        assertEquals(listOf(true), emissions)
    }

    @Test
    fun callback_emitsTrueOnValidatedAndFalseOnLost() = runTest(UnconfinedTestDispatcher()) {
        val emissions = mutableListOf<Boolean>()
        val job = launch { monitor().isOnline.toList(emissions) }

        val captor = argumentCaptor<ConnectivityManager.NetworkCallback>()
        verify(connectivityManager).registerNetworkCallback(any<NetworkRequest>(), captor.capture())
        val callback = captor.firstValue
        val network: Network = mock()

        callback.onCapabilitiesChanged(network, validatedCaps())
        // Same state again — distinctUntilChanged should swallow it.
        callback.onCapabilitiesChanged(network, validatedCaps())
        callback.onLost(network)

        job.cancel()
        assertEquals(listOf(false, true, false), emissions)
    }

    @Test
    fun callback_staysTrueIfOneOfMultipleNetworksLost() = runTest(UnconfinedTestDispatcher()) {
        val emissions = mutableListOf<Boolean>()
        val job = launch { monitor().isOnline.toList(emissions) }

        val captor = argumentCaptor<ConnectivityManager.NetworkCallback>()
        verify(connectivityManager).registerNetworkCallback(any<NetworkRequest>(), captor.capture())
        val callback = captor.firstValue
        val wifi: Network = mock()
        val cellular: Network = mock()

        callback.onCapabilitiesChanged(wifi, validatedCaps())
        callback.onCapabilitiesChanged(cellular, validatedCaps())
        // Losing one of two validated networks keeps us online — set still non-empty.
        callback.onLost(wifi)
        callback.onLost(cellular)

        job.cancel()
        assertEquals(listOf(false, true, false), emissions)
    }

    @Test
    fun cancellation_unregistersCallback() = runTest(UnconfinedTestDispatcher()) {
        val job = launch { monitor().isOnline.collect { } }

        val captor = argumentCaptor<ConnectivityManager.NetworkCallback>()
        verify(connectivityManager).registerNetworkCallback(any<NetworkRequest>(), captor.capture())

        job.cancel()
        runCurrent()
        verify(connectivityManager).unregisterNetworkCallback(captor.firstValue)
    }

    private fun validatedCaps(): NetworkCapabilities {
        val caps = mock<NetworkCapabilities>()
        whenever(caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        whenever(caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(true)
        return caps
    }

    private fun internetOnlyCaps(): NetworkCapabilities {
        val caps = mock<NetworkCapabilities>()
        whenever(caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        whenever(caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(false)
        return caps
    }
}
