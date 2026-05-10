package com.example.android.popularmovies2.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ConnectionUtilsTest {
    private val apiM = Build.VERSION_CODES.M
    private val apiPreM = Build.VERSION_CODES.LOLLIPOP

    @Test
    fun isNetworkConnected_returnsFalseWhenConnectivityServiceMissing() {
        val context = mock<Context>()
        whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(null)
        assertFalse(ConnectionUtils.isNetworkConnected(context, apiM))
    }

    @Test
    fun isNetworkConnected_returnsTrueOnWifiTransport_apiM() {
        val capabilities = mock<NetworkCapabilities>()
        whenever(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(true)
        assertTrue(ConnectionUtils.isNetworkConnected(contextWithCapabilities(capabilities), apiM))
    }

    @Test
    fun isNetworkConnected_returnsTrueOnCellularTransport_apiM() {
        val capabilities = mock<NetworkCapabilities>()
        whenever(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false)
        whenever(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true)
        assertTrue(ConnectionUtils.isNetworkConnected(contextWithCapabilities(capabilities), apiM))
    }

    @Test
    fun isNetworkConnected_returnsTrueOnEthernetTransport_apiM() {
        val capabilities = mock<NetworkCapabilities>()
        whenever(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false)
        whenever(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(false)
        whenever(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)).thenReturn(true)
        assertTrue(ConnectionUtils.isNetworkConnected(contextWithCapabilities(capabilities), apiM))
    }

    @Test
    fun isNetworkConnected_returnsFalseWhenNoTransports_apiM() {
        val capabilities = mock<NetworkCapabilities>()
        assertFalse(ConnectionUtils.isNetworkConnected(contextWithCapabilities(capabilities), apiM))
    }

    @Test
    fun isNetworkConnected_returnsFalseWhenCapabilitiesAreNull_apiM() {
        assertFalse(ConnectionUtils.isNetworkConnected(contextWithCapabilities(null), apiM))
    }

    @Test
    fun isNetworkConnected_returnsTrueOnConnectedNetworkInfo_preM() {
        val info = mock<NetworkInfo>()
        whenever(info.isConnected).thenReturn(true)
        assertTrue(ConnectionUtils.isNetworkConnected(contextWithNetworkInfo(info), apiPreM))
    }

    @Test
    fun isNetworkConnected_returnsFalseOnDisconnectedNetworkInfo_preM() {
        val info = mock<NetworkInfo>()
        whenever(info.isConnected).thenReturn(false)
        assertFalse(ConnectionUtils.isNetworkConnected(contextWithNetworkInfo(info), apiPreM))
    }

    @Test
    fun isNetworkConnected_returnsFalseWhenNetworkInfoNull_preM() {
        assertFalse(ConnectionUtils.isNetworkConnected(contextWithNetworkInfo(null), apiPreM))
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

    @Suppress("DEPRECATION")
    private fun contextWithNetworkInfo(info: NetworkInfo?): Context {
        val context = mock<Context>()
        val manager = mock<ConnectivityManager>()
        whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager)
        whenever(manager.activeNetworkInfo).thenReturn(info)
        return context
    }
}
