package com.supershade.domain.tile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.location.LocationManager
import android.os.Build

/** Permission required by Android before exposing the connected Wi-Fi SSID. */
fun Context.hasWifiNetworkNamePermission(): Boolean {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.NEARBY_WIFI_DEVICES
    } else {
        Manifest.permission.ACCESS_FINE_LOCATION
    }
    return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.wifiNetworkNameAccessHint(): String? {
    if (!hasWifiNetworkNamePermission()) return "Permission needed"
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        val locationManager = getSystemService(LocationManager::class.java)
        if (locationManager?.isLocationEnabled != true) return "Turn on Location"
    }
    return null
}

@Suppress("DEPRECATION")
fun Context.connectedWifiInfo(): WifiInfo? {
    if (!hasWifiNetworkNamePermission()) return null

    val wifiManager = getSystemService(Context.WIFI_SERVICE) as? WifiManager ?: return null
    if (!wifiManager.isWifiEnabled) return null

    return try {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectivityManager?.activeNetwork
            ?.let { connectivityManager.getNetworkCapabilities(it)?.transportInfo as? WifiInfo }
        networkInfo ?: wifiManager.connectionInfo
    } catch (_: SecurityException) {
        null
    } catch (_: Exception) {
        null
    }
}

fun Context.connectedWifiSsid(): String? {
    val ssid = connectedWifiInfo()?.ssid?.removeSurrounding("\"") ?: return null
    return ssid.takeIf { it.isNotBlank() && it != WifiManager.UNKNOWN_SSID }
}
