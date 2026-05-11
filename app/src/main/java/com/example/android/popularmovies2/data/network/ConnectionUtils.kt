/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Returns `true` only when the device has *validated* internet right now.
 *
 * ## Why capabilities, not transports
 *
 * The old version of this file (and most StackOverflow answers) checks
 * **transports**: "is `TRANSPORT_WIFI` / `TRANSPORT_CELLULAR` / `TRANSPORT_ETHERNET`
 * present?". That answers "is there a radio attached", not "can I reach the internet".
 * Two cases the transport check gets wrong:
 *
 *  - **Captive portals** (hotel / airport / coffee-shop WiFi): the user is on WiFi,
 *    every HTTP request gets rewritten to a login page. `TRANSPORT_WIFI` is true.
 *    Real internet is not.
 *  - **VPN**: the active network's transport is `TRANSPORT_VPN`, not WiFi/cellular,
 *    so the old transport check returned `false` even though traffic clearly works.
 *
 * Use **capabilities** on the active network instead:
 *
 *  - [NetworkCapabilities.NET_CAPABILITY_INTERNET] — "this network is *supposed* to
 *    provide internet routing".
 *  - [NetworkCapabilities.NET_CAPABILITY_VALIDATED] — "Android already probed the
 *    network (default: `connectivitycheck.gstatic.com/generate_204`) and got a real
 *    response back". This is the flag that flips off behind a captive portal, a broken
 *    DNS, an expired cellular plan, etc.
 *
 * Both true ⇒ outbound requests will (probably) work. That's what we want here.
 *
 * ## "But I need to know if it's WiFi vs mobile"
 *
 * That's a different question — usually asked because you want to avoid burning the
 * user's cellular data. The right capability for that is
 * `!caps.hasCapability(NET_CAPABILITY_NOT_METERED)` — it covers cellular *and* metered
 * WiFi hotspots, which a raw `hasTransport(TRANSPORT_CELLULAR)` check would miss.
 * Use `hasTransport(...)` only when you genuinely need the radio type (e.g. surfacing
 * "You're on cellular" in UI), not for billing decisions.
 *
 * ## Polling vs. a callback
 *
 * This function is **synchronous**: it samples connectivity at the instant of the
 * call. That's fine for gating a one-shot user action — here, the menu tap in
 * `MainActivity` that decides whether to fetch from TMDb or fall back to favorites.
 *
 * It is **wrong** for keeping UI in sync with connectivity over time. For that, register
 * a [ConnectivityManager.NetworkCallback] — typically wrapped in a `callbackFlow<Boolean>`
 * exposed from a repository, and collected from a `ViewModel` — so you receive push
 * events on connect / disconnect / validation-change instead of polling on every render.
 */
object ConnectionUtils {
    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            ?: return false
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
