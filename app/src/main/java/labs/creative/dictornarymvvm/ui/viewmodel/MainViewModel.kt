package labs.creative.dictornarymvvm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import labs.creative.dictornarymvvm.data.preferences.UserPreferencesRepository
import labs.creative.dictornarymvvm.domain.model.WordInfo
import labs.creative.dictornarymvvm.domain.usecase.GetWordInfoUseCase
import labs.creative.dictornarymvvm.ui.adapter.TrendingWord
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getWordInfoUseCase: GetWordInfoUseCase,
) : ViewModel() {

    private val _wordOfTheDay = MutableStateFlow<WordInfo?>(null)
    val wordOfTheDay: StateFlow<WordInfo?> get() = _wordOfTheDay

    private val _trendingWords = MutableStateFlow<List<TrendingWord>>(emptyList())
    val trendingWords: StateFlow<List<TrendingWord>> get() = _trendingWords

    val recentWords: StateFlow<List<String>> = userPreferencesRepository.recentWords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(TIMEOUT_MS), emptyList())

    private val wordOfTheDayPool = listOf(
        "ephemeral",
        "serendipity",
        "ubiquitous",
        "eloquent",
        "melancholy",
        "resilient",
        "paradigm",
        "aesthetic",
        "catalyst",
        "nuance",
        "pragmatic",
        "resilience",
        "candid",
        "ambiguous",
        "meticulous",
        "tenacious",
        "whimsical",
        "cognizant",
        "labyrinth",
        "quintessential",
    )

    companion object {
        private const val TIMEOUT_MS = 5000L
    }

    init {
        loadMvpData()
    }

    @Suppress("TooGenericExceptionCaught")
    private fun loadMvpData() {
        viewModelScope.launch {
            try {
                val dayOfYear = LocalDate.now().dayOfYear
                val word = wordOfTheDayPool[dayOfYear % wordOfTheDayPool.size]
                val results = getWordInfoUseCase(word)
                _wordOfTheDay.value = results.firstOrNull()
            } catch (e: kotlinx.coroutines.CancellationException) {
                // Must propagate so the coroutine/viewModelScope can unwind properly
                // (e.g. when the ViewModel is cleared while the request is in flight).
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Failed to load word of the day")
                _wordOfTheDay.value = null
            }
        }

        _trendingWords.value = listOf(
            TrendingWord(
                rank = "01",
                word = "Serendipity",
                description = "The occurrence of events by chance in a happy or beneficial way.",
            ),
            TrendingWord(
                rank = "02",
                word = "Ubiquitous",
                description = "Present, appearing, or found everywhere.",
            ),
            TrendingWord(
                rank = "03",
                word = "Eloquent",
                description = "Fluent or persuasive in speaking or writing.",
            ),
            TrendingWord(
                rank = "04",
                word = "Melancholy",
                description = "A feeling of pensive sadness, typically with no obvious cause.",
            ),
            TrendingWord(
                rank = "05",
                word = "Resilient",
                description = "Able to withstand or recover quickly from difficult conditions.",
            ),
        )
    }
}
