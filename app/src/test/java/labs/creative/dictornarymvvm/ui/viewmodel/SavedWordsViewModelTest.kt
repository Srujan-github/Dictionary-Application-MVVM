package labs.creative.dictornarymvvm.ui.viewmodel

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import labs.creative.dictornarymvvm.domain.model.SavedWord
import labs.creative.dictornarymvvm.domain.usecase.DeleteSavedWordUseCase
import labs.creative.dictornarymvvm.domain.usecase.GetSavedWordsUseCase
import labs.creative.dictornarymvvm.domain.usecase.SaveWordUseCase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SavedWordsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getSavedWordsUseCase: GetSavedWordsUseCase = mockk()
    private val deleteSavedWordUseCase: DeleteSavedWordUseCase = mockk(relaxed = true)
    private val saveWordUseCase: SaveWordUseCase = mockk(relaxed = true)

    private val sampleWords = listOf(
        SavedWord(
            word = "ephemeral",
            phonetic = "/ɪˈfɛm(ə)rəl/",
            partOfSpeech = "adjective",
            shortDefinition = "Lasting for a very short time.",
            savedAt = 1_000L,
        ),
        SavedWord(
            word = "serendipity",
            phonetic = "/ˌsɛrənˈdɪpɪti/",
            partOfSpeech = "noun",
            shortDefinition = "The occurrence of events by chance in a happy way.",
            savedAt = 2_000L,
        ),
    )

    private lateinit var sut: SavedWordsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getSavedWordsUseCase() } returns flowOf(sampleWords)
        sut = SavedWordsViewModel(getSavedWordsUseCase, deleteSavedWordUseCase, saveWordUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `savedWords - reflects list emitted by GetSavedWordsUseCase`() = runTest {
        val job = launch { sut.savedWords.collect { } }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(sampleWords, sut.savedWords.value)
        job.cancel()
    }

    @Test
    fun `deleteWord - invokes DeleteSavedWordUseCase with the given word`() = runTest {
        sut.deleteWord("ephemeral")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { deleteSavedWordUseCase("ephemeral") }
    }

    @Test
    fun `saveWord - invokes SaveWordUseCase with the given SavedWord`() = runTest {
        val word = sampleWords.first()

        sut.saveWord(word)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { saveWordUseCase(word) }
    }
}
