package labs.creative.dictornarymvvm.core

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * A [Timber.Tree] that forwards logs to Firebase Crashlytics for release builds.
 */
class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        val crashlytics = FirebaseCrashlytics.getInstance()

        if (t == null) {
            crashlytics.log(if (tag != null) "$tag: $message" else message)
        } else {
            crashlytics.log(if (tag != null) "$tag: $message" else message)
            if (priority >= Log.ERROR) {
                crashlytics.recordException(t)
            }
        }
    }
}
