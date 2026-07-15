package labs.creative.dictornarymvvm.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
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
    val APP_LANGUAGE = stringPreferencesKey("app_language")
}

private const val MAX_RECENT_WORDS = 10
private const val DEFAULT_APP_LANGUAGE = "English"

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationContext private val context: Context,
) {
    companion object {
        private const val DARK_MODE_MIRROR_PREFS_NAME = "dark_mode_mirror"
        private const val DARK_MODE_MIRROR_KEY = "dark_mode_enabled"
    }

    val isDarkModeEnabled: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[PreferenceKeys.DARK_MODE_ENABLED] ?: false }

    val isDailyWordEnabled: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[PreferenceKeys.DAILY_WORD_ENABLED] ?: true }

    val appLanguage: Flow<String> = dataStore.data
        .map { prefs -> prefs[PreferenceKeys.APP_LANGUAGE] ?: DEFAULT_APP_LANGUAGE }

    val recentWords: Flow<List<String>> = dataStore.data
        .map { prefs ->
            val raw = prefs[PreferenceKeys.RECENT_WORDS] ?: ""
            if (raw.isBlank()) emptyList() else raw.split(",")
        }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[PreferenceKeys.DARK_MODE_ENABLED] = enabled }
        // Write-through mirror: lets callers (e.g. MainActivity.onCreate) read the
        // current value synchronously without blocking on DataStore disk I/O.
        context.getSharedPreferences(DARK_MODE_MIRROR_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(DARK_MODE_MIRROR_KEY, enabled)
            .apply()
    }

    /**
     * Synchronous read of the dark-mode flag from the SharedPreferences mirror,
     * kept in sync by [setDarkMode]. Safe to call on the main thread (e.g. before
     * `setContentView()` in `onCreate`) since it never touches DataStore's disk I/O.
     * Defaults to `false` to match [isDarkModeEnabled]'s DataStore default.
     */
    fun isDarkModeEnabledSync(): Boolean {
        return context.getSharedPreferences(DARK_MODE_MIRROR_PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(DARK_MODE_MIRROR_KEY, false)
    }

    suspend fun setDailyWord(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[PreferenceKeys.DAILY_WORD_ENABLED] = enabled }
    }

    suspend fun setAppLanguage(language: String) {
        dataStore.edit { prefs -> prefs[PreferenceKeys.APP_LANGUAGE] = language }
    }

    suspend fun addRecentWord(word: String) {
        val trimmed = word.trim()
        // Guard against blank input corrupting the CSV list with an empty entry.
        if (trimmed.isEmpty()) return
        dataStore.edit { prefs ->
            val raw = prefs[PreferenceKeys.RECENT_WORDS] ?: ""
            val currentList = if (raw.isBlank()) emptyList() else raw.split(",")
            // Remove any case-insensitive match so re-searching "Hello" after "hello"
            // moves the existing entry to top instead of creating a duplicate chip.
            val newList = listOf(trimmed) + currentList.filter { !it.equals(trimmed, ignoreCase = true) }
            // Keep only latest configured amount
            prefs[PreferenceKeys.RECENT_WORDS] = newList.take(MAX_RECENT_WORDS).joinToString(",")
        }
    }
}
