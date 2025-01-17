package labs.creative.dictornary_mvvm_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import labs.creative.dictornary_mvvm_app.domain.model.WordSuggestion
import labs.creative.dictornary_mvvm_app.domain.usecase.GetWordSuggestionsUseCase
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getWordSuggestionsUseCase: GetWordSuggestionsUseCase
) : ViewModel() {

    private val _wordSuggestions = MutableStateFlow<List<WordSuggestion>>(emptyList())
    val wordSuggestions: StateFlow<List<WordSuggestion>> get() = _wordSuggestions

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun fetchWordSuggestions(meaning: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null  // Reset error state before starting the request

            try {
                val suggestions = getWordSuggestionsUseCase(meaning)
                _wordSuggestions.value = suggestions
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "An unexpected error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
