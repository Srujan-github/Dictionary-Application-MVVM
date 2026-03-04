package labs.creative.dictornarymvvm.core

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Application-scoped TTS manager injected via Hilt.
 * Handles async TTS initialisation and guards speak() calls until ready.
 */
@Singleton
class TtsManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    /** Call once per screen (idempotent after first call). */
    fun init() {
        if (tts != null) return
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isReady = true
            }
        }
    }

    /** Speaks [word]. No-op if TTS is not yet ready. */
    fun speak(word: String) {
        if (!isReady) return
        tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
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
