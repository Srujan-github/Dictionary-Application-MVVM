package labs.creative.dictornarymvvm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.data.preferences.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    companion object {
        private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L
    }

    val isDarkModeEnabled: StateFlow<Boolean> = userPreferencesRepository.isDarkModeEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), false)

    val isDailyWordEnabled: StateFlow<Boolean> = userPreferencesRepository.isDailyWordEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), true)

    val appLanguage: StateFlow<String> = userPreferencesRepository.appLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS), "English")

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setDarkMode(enabled) }
    }

    fun toggleDailyWord(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setDailyWord(enabled) }
    }

    fun setAppLanguage(language: String) {
        viewModelScope.launch { userPreferencesRepository.setAppLanguage(language) }
    }
}
