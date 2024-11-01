package labs.creative.dictornary_mvvm_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import labs.creative.dictornary_mvvm_app.domain.model.WordInfo
import labs.creative.dictornary_mvvm_app.domain.model.WordSuggestion
import labs.creative.dictornary_mvvm_app.domain.usecase.GetWordInfoUseCase
import labs.creative.dictornary_mvvm_app.domain.usecase.GetWordSuggestionsUseCase
import javax.inject.Inject


@HiltViewModel
class WordViewModel @Inject constructor(
    private val getWordSuggestionsUseCase: GetWordSuggestionsUseCase,
    private val getWordInfoUseCase: GetWordInfoUseCase
) : ViewModel() {

    private val _wordSuggestions = MutableStateFlow<Resource<List<WordSuggestion>>>(Resource.Loading())
    val wordSuggestions: StateFlow<Resource<List<WordSuggestion>>> = _wordSuggestions

    private val _wordInfo = MutableStateFlow<Resource<List<WordInfo>>>(Resource.Loading())
    val wordInfo: StateFlow<Resource<List<WordInfo>>> = _wordInfo

    fun fetchWordSuggestions(meaning: String) {
        viewModelScope.launch {
            _wordSuggestions.value = Resource.Loading()
            try {
                val suggestions = getWordSuggestionsUseCase(meaning)
                _wordSuggestions.value = Resource.Success(suggestions)
            } catch (e: Exception) {
                _wordSuggestions.value = Resource.Error("Failed to load suggestions: ${e.message}")
            }
        }
    }

    fun fetchWordInfo(word: String) {
        viewModelScope.launch {
            _wordInfo.value = Resource.Loading()
            try {
                val info = getWordInfoUseCase(word)
                _wordInfo.value = Resource.Success(info)
            } catch (e: Exception) {
                _wordInfo.value = Resource.Error("Failed to load word info: ${e.message}")
            }
        }
    }
}
