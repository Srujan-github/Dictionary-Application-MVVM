package labs.creative.dictornarymvvm.core

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Application-scoped TTS manager injected via Hilt.
 * Handles async TTS initialisation and guards speak() calls until ready.
 *
 * [ttsFactory] is injectable for testing — production code uses the default
 * that constructs a real [TextToSpeech].
 */
@Singleton
class TtsManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    // Overrideable in tests to inject a mock TextToSpeech
    internal var ttsFactory: (Context, TextToSpeech.OnInitListener) -> TextToSpeech = ::TextToSpeech

    internal var tts: TextToSpeech? = null
    internal var isReady = false

    /** Call once per screen (idempotent after first call). */
    fun init() {
        if (tts != null) return
        tts = ttsFactory(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isReady = true
            }
        }
    }

    /**
     * Speaks [word]. No-op if TTS is not yet ready.
     *
     * If the TTS engine process was killed by the OS ([android.os.DeadObjectException]), the
     * dead instance is discarded, TTS is reinitialized, and the word is spoken automatically
     * once the engine reconnects — so the user does not have to tap twice.
     */
    fun speak(word: String) {
        if (!isReady) return
        try {
            tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
        } catch (e: android.os.DeadObjectException) {
            // Known-harmless: the TTS engine process was killed by the OS mid-call. This is
            // recovered automatically below (reinitialize + auto-retry), so it's not an error —
            // log at warning level only so it remains visible for diagnostics without alarming.
            Timber.w(e, "TTS engine process died (DeadObjectException); reinitializing")
            // TTS engine process was killed; drop the dead binder and reinitialize
            tts?.shutdown()
            tts = null
            isReady = false
            tts = ttsFactory(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.US
                    isReady = true
                    // Retry automatically with the same word. Guarded the same way as the
                    // initial attempt above so a second, immediate engine death (rare, but
                    // seen on some OEM TTS implementations) can't throw out of this
                    // OnInitListener callback and crash the app on the main thread.
                    try {
                        tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
                    } catch (e: android.os.DeadObjectException) {
                        Timber.w(e, "TTS engine died again during recovery retry; giving up for this word")
                    }
                }
            }
        }
    }

    /**
     * Release TTS resources. Should only be called when the app is being fully destroyed
     * (e.g. in [Application.onTerminate]), not per-fragment.
     */
    fun shutdown() {
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
