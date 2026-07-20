package labs.creative.dictornarymvvm

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import labs.creative.dictornarymvvm.core.AdsManager
import labs.creative.dictornarymvvm.core.CrashlyticsTree
import labs.creative.dictornarymvvm.core.NotificationChannels
import labs.creative.dictornarymvvmapp.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
        AdsManager.initialize(this)
        NotificationChannels.createDailyWordChannel(this)
    }
}
