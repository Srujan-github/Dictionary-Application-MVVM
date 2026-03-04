package labs.creative.dictornarymvvm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.domain.model.SavedWord
import labs.creative.dictornarymvvm.domain.usecase.DeleteSavedWordUseCase
import labs.creative.dictornarymvvm.domain.usecase.GetSavedWordsUseCase
import javax.inject.Inject

@HiltViewModel
class SavedWordsViewModel @Inject constructor(
    getSavedWordsUseCase: GetSavedWordsUseCase,
    private val deleteSavedWordUseCase: DeleteSavedWordUseCase,
) : ViewModel() {

    companion object {
        private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L
    }

    val savedWords: StateFlow<List<SavedWord>> = getSavedWordsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
            initialValue = emptyList(),
        )

    fun deleteWord(word: String) {
        viewModelScope.launch {
            deleteSavedWordUseCase(word)
        }
    }
}
