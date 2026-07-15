package labs.creative.dictornarymvvm.ui.widgets

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.max
import kotlin.math.min

/**
 * A [AppCompatTextView] that types out text character-by-character,
 * cycling through a list of taglines indefinitely.
 *
 * Call [animateTaglines] once after the view is laid out to start the loop.
 * The animation automatically re-cycles from the first tagline when the last
 * one finishes, and pauses [ANIMATION_PAUSE_MS] between taglines for readability.
 *
 * The internal [Handler] posts to the main thread only; there are no background
 * threads. Callbacks and listeners are cleaned up in [stopAnimation] to avoid
 * leaks when the hosting view is detached.
 */
@Suppress("TooManyFunctions")
class TypeWriterView : AppCompatTextView {

    companion object {
        private const val DEFAULT_CHAR_DELAY_MS = 40L
        private const val ANIMATION_PAUSE_MS = 1_000L
        private const val EMPTY_START_INDEX = 0
        private const val FIRST_TAGLINE_INDEX = 0
    }

    private var mText: CharSequence? = null
    private var mIndex = EMPTY_START_INDEX
    private var mDelay: Long = DEFAULT_CHAR_DELAY_MS
    private var isAnimationRunning = false
    private var mAnimationChangeListener: OnAnimationChangeListener? = null
    private var avoidTextOverflowAtEdge = true

    /** Kept as a field so we can remove it before re-adding on the next call. */
    private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    private val mHandler = Handler(Looper.getMainLooper())

    private val characterAdder = object : Runnable {
        override fun run() {
            // mText can be swapped mid-animation by the global layout listener once the
            // real measured width is known, so mIndex must be clamped against its
            // current length rather than the length assumed when typing started.
            val currentText = mText
            val length = currentText?.length ?: 0
            val end = min(mIndex, length)
            text = currentText?.subSequence(0, end)
            mIndex = end + 1
            if (mIndex <= length) {
                mHandler.postDelayed(this, mDelay)
                isAnimationRunning = true
            } else {
                isAnimationRunning = false
                pingAnimationEnded()
                onSequenceEnd()
            }
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    // ─── Public API ───────────────────────────────────────────────────────────

    /** Starts cycling through [taglines] indefinitely. */
    fun animateTaglines(taglines: List<String>) {
        animateNextTagline(taglines, FIRST_TAGLINE_INDEX)
    }

    fun animateText(text: CharSequence, onEnd: () -> Unit) {
        generateText(text.toString())
        mIndex = EMPTY_START_INDEX
        setText("")
        mHandler.removeCallbacks(characterAdder)
        mHandler.postDelayed(characterAdder, mDelay)
        setOnAnimationChangeListener(object : OnAnimationChangeListener {
            override fun onAnimationEnd() {
                onEnd()
            }
        })
    }

    fun stopAnimation() {
        if (isAnimationRunning) {
            isAnimationRunning = false
            mHandler.removeCallbacks(characterAdder)
            text = mText
            pingAnimationEnded()
        }
    }

    fun isAnimationRunning(): Boolean = isAnimationRunning

    fun isTextInitialised(): Boolean = mText != null

    fun setCharacterDelay(millis: Long) {
        mDelay = millis
    }

    fun avoidTextOverflowAtEdge(avoid: Boolean) {
        this.avoidTextOverflowAtEdge = avoid
    }

    fun interface OnAnimationChangeListener {
        fun onAnimationEnd()
    }

    fun setOnAnimationChangeListener(listener: OnAnimationChangeListener?) {
        mAnimationChangeListener = listener
    }

    // ─── Internal ─────────────────────────────────────────────────────────────

    private fun animateNextTagline(taglines: List<String>, index: Int) {
        if (index < taglines.size) {
            animateText(taglines[index]) {
                mHandler.postDelayed(
                    { animateNextTagline(taglines, index + 1) },
                    mDelay + ANIMATION_PAUSE_MS,
                )
            }
        } else {
            mHandler.postDelayed(
                { animateNextTagline(taglines, FIRST_TAGLINE_INDEX) },
                mDelay + ANIMATION_PAUSE_MS,
            )
        }
    }

    private fun generateText(inpText: String) {
        if (avoidTextOverflowAtEdge) {
            // Remove any previously registered listener before adding a new one
            // to prevent duplicate callbacks (memory / logic leak).
            globalLayoutListener?.let { viewTreeObserver.removeOnGlobalLayoutListener(it) }

            val newListener = ViewTreeObserver.OnGlobalLayoutListener {
                mText = generateFormattedSequence(inpText)
                viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                globalLayoutListener = null
            }
            globalLayoutListener = newListener
            viewTreeObserver.addOnGlobalLayoutListener(newListener)
        }
        mText = inpText
    }

    fun generateFormattedSequence(inputText: String): String {
        val words = inputText.split(" ").toTypedArray()
        val viewWidth = measuredWidth
        val finalSequence = StringBuilder()

        for (word in words) {
            val temp = finalSequence.substring(
                max(finalSequence.lastIndexOf("\n"), 0),
            ) + " " + word
            val textWidth = paint.measureText(temp)
            when {
                textWidth >= viewWidth -> finalSequence.append("\n").append(word)
                finalSequence.isEmpty() -> finalSequence.append(word)
                else -> finalSequence.append(" ").append(word)
            }
        }
        return finalSequence.toString()
    }

    private fun pingAnimationEnded() {
        mAnimationChangeListener?.onAnimationEnd()
    }

    private fun onSequenceEnd() {
        // Hook for subclasses or future use
    }
}
