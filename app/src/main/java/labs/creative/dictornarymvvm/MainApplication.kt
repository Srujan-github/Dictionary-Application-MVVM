package labs.creative.dictornarymvvm

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import labs.creative.dictornarymvvm.data.preferences.UserPreferencesRepository

@HiltAndroidApp
class MainApplication : Application() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MainApplicationEntryPoint {
        fun userPreferencesRepository(): UserPreferencesRepository
    }

    override fun onCreate() {
        super.onCreate()
        applyDarkModePreference()
    }

    private fun applyDarkModePreference() {
        val entryPoint = EntryPointAccessors.fromApplication(
            this,
            MainApplicationEntryPoint::class.java,
        )
        val isDarkMode = runBlocking {
            entryPoint.userPreferencesRepository().isDarkModeEnabled.first()
        }
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO,
        )
    }
}
