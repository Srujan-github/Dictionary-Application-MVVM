package labs.creative.dictornarymvvm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.domain.model.SavedWord
import labs.creative.dictornarymvvm.domain.model.WordInfo
import labs.creative.dictornarymvvm.domain.usecase.DeleteSavedWordUseCase
import labs.creative.dictornarymvvm.domain.usecase.GetWordInfoUseCase
import labs.creative.dictornarymvvm.domain.usecase.IsWordSavedUseCase
import labs.creative.dictornarymvvm.domain.usecase.SaveWordUseCase
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val getWordInfoUseCase: GetWordInfoUseCase,
    private val saveWordUseCase: SaveWordUseCase,
    private val deleteWordUseCase: DeleteSavedWordUseCase,
    private val isWordSavedUseCase: IsWordSavedUseCase,
) : ViewModel() {

    private val _wordInfo = MutableStateFlow<WordInfo?>(null)
    val wordInfo: StateFlow<WordInfo?> get() = _wordInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> get() = _isSaved

    @Suppress("TooGenericExceptionCaught")
    fun fetchWordInfo(word: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val results = getWordInfoUseCase(word)
                _wordInfo.value = results.firstOrNull()
                // Check if already saved
                _isSaved.value = isWordSavedUseCase(word)
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.localizedMessage ?: "Could not load word information."
                throw e
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleSave() {
        val info = _wordInfo.value ?: return
        viewModelScope.launch {
            if (_isSaved.value) {
                deleteWordUseCase(info.word)
                _isSaved.value = false
            } else {
                saveWordUseCase(
                    SavedWord(
                        word = info.word,
                        phonetic = info.phonetic,
                        partOfSpeech = info.partOfSpeech,
                        shortDefinition = info.definitions.firstOrNull() ?: "",
                        savedAt = System.currentTimeMillis(),
                    ),
                )
                _isSaved.value = true
            }
        }
    }
}
