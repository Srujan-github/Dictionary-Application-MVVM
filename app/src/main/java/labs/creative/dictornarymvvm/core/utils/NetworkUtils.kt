package labs.creative.dictornarymvvm.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * Utility helpers for network connectivity checks.
 */
object NetworkUtils {

    /**
     * Returns `true` if the device has an active network connection with
     * either WIFI, cellular, or ethernet transport.
     *
     * Uses [NetworkCapabilities] on API 23+ (the [ConnectivityManager.activeNetworkInfo]
     * API was deprecated in API 29 and removed in API 31).
     * Falls back to the legacy path only on API 21–22 (minSdk is 24, so the
     * fallback branch is never reached in practice and is kept only for
     * safety/lint suppression).
     */
    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val caps = cm?.activeNetwork?.let { cm.getNetworkCapabilities(it) }
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
                caps?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
        } else {
            @Suppress("DEPRECATION")
            cm?.activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }
}
