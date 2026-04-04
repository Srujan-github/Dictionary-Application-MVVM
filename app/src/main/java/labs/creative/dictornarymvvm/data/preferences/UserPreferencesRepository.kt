package labs.creative.dictornarymvvm.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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
    val RECENT_WORDS = stringPreferencesKey("recent_words")
}

private const val MAX_RECENT_WORDS = 10

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val isDarkModeEnabled: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[PreferenceKeys.DARK_MODE_ENABLED] ?: false }

    val isDailyWordEnabled: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[PreferenceKeys.DAILY_WORD_ENABLED] ?: true }

    val recentWords: Flow<List<String>> = dataStore.data
        .map { prefs ->
            val raw = prefs[PreferenceKeys.RECENT_WORDS] ?: ""
            if (raw.isBlank()) emptyList() else raw.split(",")
        }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[PreferenceKeys.DARK_MODE_ENABLED] = enabled }
    }

    suspend fun setDailyWord(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[PreferenceKeys.DAILY_WORD_ENABLED] = enabled }
    }

    suspend fun addRecentWord(word: String) {
        dataStore.edit { prefs ->
            val raw = prefs[PreferenceKeys.RECENT_WORDS] ?: ""
            val currentList = if (raw.isBlank()) emptyList() else raw.split(",")
            // Remove if exists to move to top
            val newList = listOf(word) + currentList.filter { it != word }
            // Keep only latest configured amount
            prefs[PreferenceKeys.RECENT_WORDS] = newList.take(MAX_RECENT_WORDS).joinToString(",")
        }
    }
}
