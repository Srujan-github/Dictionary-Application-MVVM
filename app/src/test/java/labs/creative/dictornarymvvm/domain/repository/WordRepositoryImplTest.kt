package labs.creative.dictornarymvvm.domain.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import labs.creative.dictornarymvvm.data.local.dao.SavedWordDao
import labs.creative.dictornarymvvm.data.local.entity.SavedWordEntity
import labs.creative.dictornarymvvm.data.remote.api.DatamuseApiService
import labs.creative.dictornarymvvm.data.remote.api.DictionaryApiService
import labs.creative.dictornarymvvm.data.remote.model.Definition
import labs.creative.dictornarymvvm.data.remote.model.Meaning
import labs.creative.dictornarymvvm.data.remote.model.Phonetic
import labs.creative.dictornarymvvm.data.remote.model.WordInfoDto
import labs.creative.dictornarymvvm.data.remote.model.WordSuggestionDto
import labs.creative.dictornarymvvm.domain.model.SavedWord
import labs.creative.dictornarymvvm.domain.model.WordInfo
import labs.creative.dictornarymvvm.domain.model.WordSuggestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WordRepositoryImplTest {

    private val dictionaryApiService: DictionaryApiService = mockk()
    private val datamuseApiService: DatamuseApiService = mockk()
    private val savedWordDao: SavedWordDao = mockk(relaxed = true)

    private lateinit var sut: WordRepositoryImpl

    @Before
    fun setUp() {
        sut = WordRepositoryImpl(dictionaryApiService, datamuseApiService, savedWordDao)
    }

    private val wordInfoDto = WordInfoDto(
        license = null,
        meanings = listOf(
            Meaning(
                antonyms = listOf("hate"),
                definitions = listOf(
                    Definition(
                        antonyms = null,
                        definition = "a quick brown fox",
                        example = "jumps over the lazy dog",
                        synonyms = null,
                    ),
                ),
                partOfSpeech = "noun",
                synonyms = listOf("quick"),
            ),
        ),
        phonetics = listOf(
            Phonetic(audio = "https://audio.example/fox.mp3", license = null, sourceUrl = null, text = "/fɒks/"),
        ),
        sourceUrls = null,
        word = "fox",
    )

    // ─── getWordInfo() ─────────────────────────────────────────────────────

    @Test
    fun `getWordInfo - delegates to DictionaryApiService with the given word`() = runBlocking {
        coEvery { dictionaryApiService.getWordInfo("fox") } returns listOf(wordInfoDto)

        sut.getWordInfo("fox")

        coVerify(exactly = 1) { dictionaryApiService.getWordInfo("fox") }
    }

    @Test
    fun `getWordInfo - maps DictionaryApiService DTOs to domain WordInfo`() = runBlocking {
        coEvery { dictionaryApiService.getWordInfo("fox") } returns listOf(wordInfoDto)

        val result = sut.getWordInfo("fox")

        assertEquals(
            listOf(
                WordInfo(
                    word = "fox",
                    phonetic = "/fɒks/",
                    audioUrl = "https://audio.example/fox.mp3",
                    partOfSpeech = "noun",
                    definitions = listOf("a quick brown fox"),
                    examples = listOf("jumps over the lazy dog"),
                    synonyms = listOf("quick"),
                    antonyms = listOf("hate"),
                ),
            ),
            result,
        )
    }

    // ─── getWordSuggestions() ──────────────────────────────────────────────

    @Test
    fun `getWordSuggestions - delegates to DatamuseApiService with the given meaning`() = runBlocking {
        coEvery { datamuseApiService.getWordSuggestions("cunning") } returns
            listOf(WordSuggestionDto(word = "foxy", score = 100, tags = listOf("adj")))

        sut.getWordSuggestions("cunning")

        coVerify(exactly = 1) { datamuseApiService.getWordSuggestions("cunning") }
    }

    @Test
    fun `getWordSuggestions - maps DatamuseApiService DTOs to domain WordSuggestion`() = runBlocking {
        coEvery { datamuseApiService.getWordSuggestions("cunning") } returns
            listOf(WordSuggestionDto(word = "foxy", score = 100, tags = listOf("adj")))

        val result = sut.getWordSuggestions("cunning")

        assertEquals(listOf(WordSuggestion(word = "foxy", score = 100)), result)
    }

    // ─── getSavedWords() ────────────────────────────────────────────────────

    @Test
    fun `getSavedWords - maps DAO entities to domain SavedWord list`() = runBlocking {
        every { savedWordDao.getAllSavedWords() } returns flowOf(
            listOf(
                SavedWordEntity(
                    word = "fox",
                    phonetic = "/fɒks/",
                    partOfSpeech = "noun",
                    shortDefinition = "a quick brown fox",
                    savedAt = 123L,
                ),
            ),
        )

        val result = sut.getSavedWords().first()

        assertEquals(
            listOf(
                SavedWord(
                    word = "fox",
                    phonetic = "/fɒks/",
                    partOfSpeech = "noun",
                    shortDefinition = "a quick brown fox",
                    savedAt = 123L,
                ),
            ),
            result,
        )
    }

    // ─── saveWord() ─────────────────────────────────────────────────────────

    @Test
    fun `saveWord - persists a SavedWordEntity built from the domain SavedWord`() = runBlocking {
        val entitySlot = slot<SavedWordEntity>()
        val word = SavedWord(
            word = "fox",
            phonetic = "/fɒks/",
            partOfSpeech = "noun",
            shortDefinition = "a quick brown fox",
            savedAt = 123L,
        )

        sut.saveWord(word)

        coVerify(exactly = 1) { savedWordDao.saveWord(capture(entitySlot)) }
        assertEquals(
            SavedWordEntity(
                word = "fox",
                phonetic = "/fɒks/",
                partOfSpeech = "noun",
                shortDefinition = "a quick brown fox",
                savedAt = 123L,
            ),
            entitySlot.captured,
        )
    }

    // ─── deleteWord() ───────────────────────────────────────────────────────

    @Test
    fun `deleteWord - delegates to DAO deleteWordByName with the given word`() = runBlocking {
        sut.deleteWord("fox")

        coVerify(exactly = 1) { savedWordDao.deleteWordByName("fox") }
    }

    // ─── isWordSaved() ──────────────────────────────────────────────────────

    @Test
    fun `isWordSaved - returns true when DAO reports the word is saved`() = runBlocking {
        coEvery { savedWordDao.isWordSaved("fox") } returns true

        val result = sut.isWordSaved("fox")

        assertTrue(result)
    }

    @Test
    fun `isWordSaved - returns false when DAO reports the word is not saved`() = runBlocking {
        coEvery { savedWordDao.isWordSaved("fox") } returns false

        val result = sut.isWordSaved("fox")

        assertFalse(result)
    }
}
