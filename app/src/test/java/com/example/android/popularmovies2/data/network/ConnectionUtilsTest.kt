package com.example.android.popularmovies2.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ConnectionUtilsTest {
    @Test
    fun isNetworkConnected_returnsFalseWhenConnectivityServiceMissing() {
        val context = mock<Context>()
        whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(null)
        assertFalse(ConnectionUtils.isNetworkConnected(context))
    }

    @Test
    fun isNetworkConnected_returnsTrueWhenInternetAndValidated() {
        val capabilities = mock<NetworkCapabilities>()
        whenever(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        whenever(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(true)
        assertTrue(ConnectionUtils.isNetworkConnected(contextWithCapabilities(capabilities)))
    }

    @Test
    fun isNetworkConnected_returnsFalseWhenInternetButNotValidated() {
        // Captive portal: network advertises internet but Android's probe failed.
        val capabilities = mock<NetworkCapabilities>()
        whenever(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
        whenever(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(false)
        assertFalse(ConnectionUtils.isNetworkConnected(contextWithCapabilities(capabilities)))
    }

    @Test
    fun isNetworkConnected_returnsFalseWhenNoInternetCapability() {
        val capabilities = mock<NetworkCapabilities>()
        whenever(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(false)
        whenever(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(true)
        assertFalse(ConnectionUtils.isNetworkConnected(contextWithCapabilities(capabilities)))
    }

    @Test
    fun isNetworkConnected_returnsFalseWhenCapabilitiesNull() {
        assertFalse(ConnectionUtils.isNetworkConnected(contextWithCapabilities(null)))
    }

    private fun contextWithCapabilities(capabilities: NetworkCapabilities?): Context {
        val context = mock<Context>()
        val manager = mock<ConnectivityManager>()
        val network = mock<Network>()
        whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager)
        whenever(manager.activeNetwork).thenReturn(network)
        whenever(manager.getNetworkCapabilities(network)).thenReturn(capabilities)
        return context
    }
}
