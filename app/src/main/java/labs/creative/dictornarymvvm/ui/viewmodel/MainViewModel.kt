package labs.creative.dictornarymvvm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import labs.creative.dictornarymvvm.data.preferences.UserPreferencesRepository
import labs.creative.dictornarymvvm.domain.model.WordInfo
import labs.creative.dictornarymvvm.ui.adapter.TrendingWord
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _wordOfTheDay = MutableStateFlow<WordInfo?>(null)
    val wordOfTheDay: StateFlow<WordInfo?> get() = _wordOfTheDay

    private val _trendingWords = MutableStateFlow<List<TrendingWord>>(emptyList())
    val trendingWords: StateFlow<List<TrendingWord>> get() = _trendingWords

    val recentWords: StateFlow<List<String>> = userPreferencesRepository.recentWords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(TIMEOUT_MS), emptyList())

    companion object {
        private const val TIMEOUT_MS = 5000L
    }

    init {
        loadMvpData()
    }

    private fun loadMvpData() {
        _wordOfTheDay.value = WordInfo(
            word = "Ephemeral",
            phonetic = "/ih-FEM-er-uhl/",
            audioUrl = null,
            partOfSpeech = "adjective",
            definitions = listOf(
                "Lasting for a very short time. Something that is fleeting, transitory, or brief in duration.",
            ),
            examples = listOf(
                "The beauty of cherry blossoms is ephemeral, lasting only a few days each spring.",
                "Fame can be ephemeral, here today and gone tomorrow.",
            ),
            synonyms = listOf("fleeting", "transient", "momentary", "brief", "passing"),
            antonyms = listOf("permanent", "enduring", "lasting", "eternal"),
        )

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
