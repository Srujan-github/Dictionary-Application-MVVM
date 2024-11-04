package labs.creative.dictornary_mvvm_app.core.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtils {
    @Suppress("Deprecation")
    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (cm != null) {
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }
        return false
    }
}