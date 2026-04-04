package labs.creative.dictornarymvvm.core

import android.content.Context
import android.os.DeadObjectException
import android.speech.tts.TextToSpeech
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.Locale

/**
 * Unit tests for [TtsManager].
 *
 * [TtsManager] accepts a [ttsFactory] lambda so tests can inject a mock
 * [TextToSpeech] directly — no [mockkConstructor] gymnastics needed.
 * Each test controls the async [TextToSpeech.OnInitListener] by capturing it
 * in a [slot] and firing it synchronously.
 */
class TtsManagerTest {

    private val context: Context = mockk(relaxed = true)

    // Mocks created fresh for each TTS "instance" the factory produces
    private lateinit var mockTts1: TextToSpeech
    private lateinit var mockTts2: TextToSpeech

    // Captures the OnInitListener the factory passes to the first TTS instance
    private val initSlot1 = slot<TextToSpeech.OnInitListener>()

    // Captures the OnInitListener for the second TTS instance (after recovery)
    private val initSlot2 = slot<TextToSpeech.OnInitListener>()

    private var factoryCallCount = 0
    private lateinit var sut: TtsManager

    @Before
    fun setUp() {
        mockTts1 = mockk(relaxed = true)
        mockTts2 = mockk(relaxed = true)
        factoryCallCount = 0
        sut = buildSutWithFactory()
    }

    /**
     * Builds a [TtsManager] with a fake factory that captures [TextToSpeech.OnInitListener]
     * per call, and returns pre-built mocks.
     */
    private fun buildSutWithFactory(): TtsManager {
        val slot1 = initSlot1
        val slot2 = initSlot2
        var count = 0

        return TtsManager(context).also { manager ->
            manager.ttsFactory = { _, listener ->
                count++
                factoryCallCount = count
                when (count) {
                    1 -> {
                        slot1.captured = listener
                        mockTts1
                    }
                    else -> {
                        slot2.captured = listener
                        mockTts2
                    }
                }
            }
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /** Calls init() and fires the first OnInitListener with [status]. */
    private fun initAndSettle(status: Int = TextToSpeech.SUCCESS) {
        sut.init()
        initSlot1.captured.onInit(status)
    }

    // ─── init() ──────────────────────────────────────────────────────────────

    @Test
    fun `init - creates TTS and fires listener on first call`() {
        sut.init()
        assert(initSlot1.isCaptured) { "Factory was not called — TTS not created" }
    }

    @Test
    fun `init - sets locale US after SUCCESS`() {
        initAndSettle(TextToSpeech.SUCCESS)
        verify { mockTts1.language = Locale.US }
    }

    @Test
    fun `init - does NOT set ready on ERROR status`() {
        initAndSettle(TextToSpeech.ERROR)
        assert(!sut.isReady) { "isReady should remain false after TTS ERROR" }
    }

    @Test
    fun `init - is idempotent, second call does not create another instance`() {
        sut.init()
        sut.init() // guard: tts != null
        assert(factoryCallCount == 1) { "Factory should be called exactly once, was: $factoryCallCount" }
    }

    // ─── speak() ─────────────────────────────────────────────────────────────

    @Test
    fun `speak - is no-op before init is called`() {
        sut.speak("hello")
        verify(exactly = 0) { mockTts1.speak(any(), any(), any(), any()) }
    }

    @Test
    fun `speak - is no-op when TTS init returned ERROR`() {
        initAndSettle(TextToSpeech.ERROR)
        sut.speak("world")
        verify(exactly = 0) { mockTts1.speak(any(), any(), any(), any()) }
    }

    @Test
    fun `speak - delegates to TTS after SUCCESS`() {
        initAndSettle(TextToSpeech.SUCCESS)
        sut.speak("ephemeral")
        verify(exactly = 1) {
            mockTts1.speak("ephemeral", TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    @Test
    fun `speak - does not throw on DeadObjectException`() {
        initAndSettle(TextToSpeech.SUCCESS)
        every { mockTts1.speak(any(), any(), any(), any()) } throws DeadObjectException()

        try {
            sut.speak("crash")
        } catch (e: Exception) {
            assert(false) { "speak() must not rethrow DeadObjectException, got: $e" }
        }
    }

    @Test
    fun `speak - shuts down dead TTS instance on DeadObjectException`() {
        initAndSettle(TextToSpeech.SUCCESS)
        every { mockTts1.speak(any(), any(), any(), any()) } throws DeadObjectException()

        sut.speak("crash")

        verify { mockTts1.shutdown() }
    }

    @Test
    fun `speak - creates a new TTS instance after DeadObjectException`() {
        initAndSettle(TextToSpeech.SUCCESS)
        every { mockTts1.speak(any(), any(), any(), any()) } throws DeadObjectException()

        sut.speak("crash")

        assert(factoryCallCount == 2) {
            "Factory should be called twice (init + recovery), was: $factoryCallCount"
        }
    }

    @Test
    fun `speak - auto-retries the word after DeadObjectException recovery`() {
        initAndSettle(TextToSpeech.SUCCESS)
        every { mockTts1.speak(any(), any(), any(), any()) } throws DeadObjectException()

        sut.speak("retry")

        // Fire SUCCESS on the new TTS instance to trigger the auto-retry
        initSlot2.captured.onInit(TextToSpeech.SUCCESS)

        verify(exactly = 1) {
            mockTts2.speak("retry", TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    @Test
    fun `speak - resets isReady to false during DeadObjectException recovery`() {
        initAndSettle(TextToSpeech.SUCCESS)
        every { mockTts1.speak(any(), any(), any(), any()) } throws DeadObjectException()

        sut.speak("crash")

        // After the crash, before the new engine reconnects, isReady must be false
        assert(!sut.isReady) { "isReady should be false during TTS recovery" }
    }

    // ─── shutdown() ───────────────────────────────────────────────────────────

    @Test
    fun `shutdown - calls TTS shutdown`() {
        initAndSettle(TextToSpeech.SUCCESS)
        sut.shutdown()
        verify { mockTts1.shutdown() }
    }

    @Test
    fun `shutdown - is safe before init`() {
        sut.shutdown() // tts == null — must not throw
    }

    @Test
    fun `shutdown - resets isReady so speak does not delegate after shutdown`() {
        initAndSettle(TextToSpeech.SUCCESS)
        sut.shutdown()

        sut.speak("after-shutdown")

        verify(exactly = 0) { mockTts1.speak(any(), any(), any(), any()) }
    }

    @Test
    fun `shutdown - allows re-initialisation with a new TTS instance`() {
        initAndSettle(TextToSpeech.SUCCESS)
        sut.shutdown() // tts = null

        sut.init() // must create a fresh instance

        assert(factoryCallCount == 2) {
            "Factory should be called twice (init + re-init after shutdown), was: $factoryCallCount"
        }
    }
}
