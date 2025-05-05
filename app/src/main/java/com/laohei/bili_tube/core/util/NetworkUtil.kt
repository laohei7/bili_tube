package com.laohei.bili_tube.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


enum class NetworkType {
    NETWORK_TYPE_UNKNOWN,
    NETWORK_TYPE_OFFLINE,
    NETWORK_TYPE_WIFI,
    NETWORK_TYPE_CELLULAR,
    NETWORK_TYPE_CELLULAR_UNKNOWN,
    NETWORK_TYPE_ETHERNET,
    NETWORK_TYPE_OTHER
}

class NetworkUtil(
    private val context: Context
) {
    fun getNetworkType(): NetworkType {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork =
            connectivityManager.activeNetwork ?: return NetworkType.NETWORK_TYPE_UNKNOWN

        val caps = connectivityManager.getNetworkCapabilities(activeNetwork)

        return caps?.run {
            when {
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.NETWORK_TYPE_WIFI
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.NETWORK_TYPE_CELLULAR
                else -> NetworkType.NETWORK_TYPE_UNKNOWN
            }
        } ?: return NetworkType.NETWORK_TYPE_UNKNOWN
    }
}