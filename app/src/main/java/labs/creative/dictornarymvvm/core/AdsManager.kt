package labs.creative.dictornarymvvm.core

import android.content.Context
import com.google.android.gms.ads.MobileAds

/**
 * Thin wrapper around the Google Mobile Ads (AdMob) SDK initialization.
 *
 * Ad unit IDs used throughout the app currently point at Google's official
 * public test IDs (see `strings.xml`: `admob_app_id`, `admob_banner_ad_unit_id`).
 * Swap those string resources for real IDs from admob.google.com before a
 * production release.
 */
object AdsManager {

    /**
     * Initializes the Mobile Ads SDK. Safe to call once, on app startup.
     */
    fun initialize(context: Context) {
        MobileAds.initialize(context)
    }
}
