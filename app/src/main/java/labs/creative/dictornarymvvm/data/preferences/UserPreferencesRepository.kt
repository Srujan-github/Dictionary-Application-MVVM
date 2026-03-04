package labs.creative.dictornarymvvm.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences",
)

object PreferenceKeys {
    val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    val DAILY_WORD_ENABLED = booleanPreferencesKey("daily_word_enabled")
}

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val isDarkModeEnabled: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[PreferenceKeys.DARK_MODE_ENABLED] ?: false }

    val isDailyWordEnabled: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[PreferenceKeys.DAILY_WORD_ENABLED] ?: true }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[PreferenceKeys.DARK_MODE_ENABLED] = enabled }
    }

    suspend fun setDailyWord(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[PreferenceKeys.DAILY_WORD_ENABLED] = enabled }
    }
}
