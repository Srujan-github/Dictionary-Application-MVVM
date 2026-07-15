package labs.creative.dictornarymvvm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.data.preferences.UserPreferencesRepository
import labs.creative.dictornarymvvm.domain.model.SavedWord
import labs.creative.dictornarymvvm.domain.model.WordInfo
import labs.creative.dictornarymvvm.domain.usecase.DeleteSavedWordUseCase
import labs.creative.dictornarymvvm.domain.usecase.GetWordInfoUseCase
import labs.creative.dictornarymvvm.domain.usecase.IsWordSavedUseCase
import labs.creative.dictornarymvvm.domain.usecase.SaveWordUseCase
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val getWordInfoUseCase: GetWordInfoUseCase,
    private val saveWordUseCase: SaveWordUseCase,
    private val deleteWordUseCase: DeleteSavedWordUseCase,
    private val isWordSavedUseCase: IsWordSavedUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    companion object {
        // Mirrors the text of R.string.word_not_found. This ViewModel has no Context/resource
        // access (matches the existing pattern in SearchViewModel, which also emits plain
        // string literals rather than resolving string resources), so the distinct "not found"
        // message is duplicated here rather than resolved via getString().
        private const val WORD_NOT_FOUND_MESSAGE = "Could not find this word. Please try again."
        private const val HTTP_NOT_FOUND = 404
    }

    private val _wordInfo = MutableStateFlow<WordInfo?>(null)
    val wordInfo: StateFlow<WordInfo?> get() = _wordInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> get() = _isSaved

    // Tracks the in-flight fetch so a new word lookup can supersede a stale one instead of
    // racing with it (both write into the same StateFlows, so whichever finished last used
    // to win — even if it was the older word's response arriving after a newer request).
    private var fetchJob: Job? = null

    @Suppress("TooGenericExceptionCaught")
    fun fetchWordInfo(word: String) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            // Reset stale save-state from any previously loaded word so the bookmark icon
            // doesn't briefly reflect the wrong word while this fetch is in flight.
            _isSaved.value = false
            try {
                val results = getWordInfoUseCase(word)
                val info = results.firstOrNull()
                if (info == null) {
                    // API returned an empty list — treat the same as a 404: word not found.
                    _error.value = WORD_NOT_FOUND_MESSAGE
                    Timber.e("word lookup failed for %s: empty result", word)
                } else {
                    _wordInfo.value = info
                    userPreferencesRepository.addRecentWord(info.word)
                    // Check if already saved
                    _isSaved.value = isWordSavedUseCase(word)
                }
            } catch (e: CancellationException) {
                // Superseded by a newer fetchWordInfo call (or the ViewModel was cleared) —
                // rethrow so structured concurrency can complete the cancellation instead of
                // this being treated as a load failure and stomping on the newer request's state.
                throw e
            } catch (e: HttpException) {
                Timber.e(e, "word lookup failed for %s", word)
                _error.value = if (e.code() == HTTP_NOT_FOUND) {
                    WORD_NOT_FOUND_MESSAGE
                } else {
                    e.localizedMessage ?: "Could not load word information."
                }
            } catch (e: Exception) {
                Timber.e(e, "word lookup failed for %s", word)
                _error.value = e.localizedMessage ?: "Could not load word information."
            } finally {
                // Guard against a cancelled (superseded) job's cleanup running after the new
                // job has already started and flipped isLoading back on — only the job that's
                // still active gets to clear the spinner.
                if (isActive) _isLoading.value = false
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
