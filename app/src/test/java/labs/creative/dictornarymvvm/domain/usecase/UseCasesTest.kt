package labs.creative.dictornarymvvm.domain.usecase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import labs.creative.dictornarymvvm.domain.model.SavedWord
import labs.creative.dictornarymvvm.domain.model.WordInfo
import labs.creative.dictornarymvvm.domain.model.WordSuggestion
import labs.creative.dictornarymvvm.domain.repository.WordRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class UseCasesTest {

    private val repository: WordRepository = mockk()

    private val sampleWordInfo = WordInfo(
        word = "fox",
        phonetic = "/fɒks/",
        audioUrl = "https://audio.example/fox.mp3",
        partOfSpeech = "noun",
        definitions = listOf("a quick brown fox"),
        examples = listOf("jumps over the lazy dog"),
        synonyms = listOf("quick"),
        antonyms = listOf("hate"),
    )

    private val sampleWordSuggestion = WordSuggestion(word = "foxy", score = 100)

    private val sampleSavedWord = SavedWord(
        word = "fox",
        phonetic = "/fɒks/",
        partOfSpeech = "noun",
        shortDefinition = "a quick brown fox",
        savedAt = 123L,
    )

    private lateinit var getWordInfoUseCase: GetWordInfoUseCase
    private lateinit var getWordSuggestionsUseCase: GetWordSuggestionsUseCase
    private lateinit var saveWordUseCase: SaveWordUseCase
    private lateinit var deleteSavedWordUseCase: DeleteSavedWordUseCase
    private lateinit var isWordSavedUseCase: IsWordSavedUseCase
    private lateinit var getSavedWordsUseCase: GetSavedWordsUseCase

    @Before
    fun setUp() {
        getWordInfoUseCase = GetWordInfoUseCase(repository)
        getWordSuggestionsUseCase = GetWordSuggestionsUseCase(repository)
        saveWordUseCase = SaveWordUseCase(repository)
        deleteSavedWordUseCase = DeleteSavedWordUseCase(repository)
        isWordSavedUseCase = IsWordSavedUseCase(repository)
        getSavedWordsUseCase = GetSavedWordsUseCase(repository)
    }

    // ─── GetWordInfoUseCase ─────────────────────────────────────────────────

    @Test
    fun `GetWordInfoUseCase - forwards word to repository and returns its result`() = runBlocking {
        coEvery { repository.getWordInfo("fox") } returns listOf(sampleWordInfo)

        val result = getWordInfoUseCase("fox")

        assertEquals(listOf(sampleWordInfo), result)
        coVerify(exactly = 1) { repository.getWordInfo("fox") }
    }

    // ─── GetWordSuggestionsUseCase ──────────────────────────────────────────

    @Test
    fun `GetWordSuggestionsUseCase - forwards meaning to repository and returns its result`() = runBlocking {
        coEvery { repository.getWordSuggestions("cunning") } returns listOf(sampleWordSuggestion)

        val result = getWordSuggestionsUseCase("cunning")

        assertEquals(listOf(sampleWordSuggestion), result)
        coVerify(exactly = 1) { repository.getWordSuggestions("cunning") }
    }

    // ─── SaveWordUseCase ────────────────────────────────────────────────────

    @Test
    fun `SaveWordUseCase - forwards SavedWord to repository`() = runBlocking {
        coEvery { repository.saveWord(sampleSavedWord) } returns Unit

        saveWordUseCase(sampleSavedWord)

        coVerify(exactly = 1) { repository.saveWord(sampleSavedWord) }
    }

    // ─── DeleteSavedWordUseCase ─────────────────────────────────────────────

    @Test
    fun `DeleteSavedWordUseCase - forwards word to repository`() = runBlocking {
        coEvery { repository.deleteWord("fox") } returns Unit

        deleteSavedWordUseCase("fox")

        coVerify(exactly = 1) { repository.deleteWord("fox") }
    }

    // ─── IsWordSavedUseCase ─────────────────────────────────────────────────

    @Test
    fun `IsWordSavedUseCase - returns true when repository reports the word is saved`() = runBlocking {
        coEvery { repository.isWordSaved("fox") } returns true

        val result = isWordSavedUseCase("fox")

        assertEquals(true, result)
        coVerify(exactly = 1) { repository.isWordSaved("fox") }
    }

    @Test
    fun `IsWordSavedUseCase - returns false when repository reports the word is not saved`() = runBlocking {
        coEvery { repository.isWordSaved("fox") } returns false

        val result = isWordSavedUseCase("fox")

        assertFalse(result)
    }

    // ─── GetSavedWordsUseCase ───────────────────────────────────────────────

    @Test
    fun `GetSavedWordsUseCase - forwards repository's saved words flow`() = runBlocking {
        every { repository.getSavedWords() } returns flowOf(listOf(sampleSavedWord))

        val result = getSavedWordsUseCase().first()

        assertEquals(listOf(sampleSavedWord), result)
        coVerify(exactly = 1) { repository.getSavedWords() }
    }
}
