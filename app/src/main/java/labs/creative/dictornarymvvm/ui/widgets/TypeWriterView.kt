package labs.creative.dictornarymvvm.ui.widgets

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView

@Suppress("TooManyFunctions")
class TypeWriterView : AppCompatTextView {
    companion object {
        private const val DEFAULT_DELAY_MS = 40L
        private const val ANIMATION_PAUSE_MS = 1000L
    }

    private var mText: CharSequence? = null
    private var mIndex = 0
    private var mDelay: Long = DEFAULT_DELAY_MS
    private var isAnimationRunning = false
    private var mAnimationChangeListener: OnAnimationChangeListener? = null
    private var avoidTextOverflowAtEdge = true
    private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private val mHandler = Handler(Looper.getMainLooper())
    private val characterAdder = object : Runnable {
        override fun run() {
            text = mText?.subSequence(0, mIndex++)
            if (mIndex <= (mText?.length ?: 0)) {
                mHandler.postDelayed(this, mDelay)
                isAnimationRunning = true
            } else {
                isAnimationRunning = false
                pingAnimationEnded()
                // Trigger next animation after current one finishes
                onAnimationEnd()
            }
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    // Start animating all the taglines one after another
    fun animateTaglines(taglines: List<String>) {
        animateNextTagline(taglines, 0)
    }

    private fun animateNextTagline(taglines: List<String>, index: Int) {
        if (index < taglines.size) {
            animateText(taglines[index]) {
                mHandler.postDelayed({
                    animateNextTagline(taglines, index + 1)
                }, mDelay + ANIMATION_PAUSE_MS)
            }
        } else {
            mHandler.postDelayed({
                animateNextTagline(taglines, 0)
            }, mDelay + ANIMATION_PAUSE_MS)
        }
    }

    fun animateText(text: CharSequence, onEnd: () -> Unit) {
        generateText(text.toString())
        mIndex = 0
        setText("")
        mHandler.removeCallbacks(characterAdder)
        mHandler.postDelayed(characterAdder, mDelay)
        setOnAnimationChangeListener(object : OnAnimationChangeListener {
            override fun onAnimationEnd() {
                onEnd() // Execute onEnd when animation ends
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

    fun isAnimationRunning(): Boolean {
        return isAnimationRunning
    }

    fun isTextInitialised(): Boolean {
        return mText != null
    }

    // To Explicitly Change the Delay
    fun setCharacterDelay(millis: Long) {
        mDelay = millis
    }

    private fun generateText(inpText: String) {
        if (avoidTextOverflowAtEdge) {
            globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
                mText = generateFormattedSequence(inpText)
                viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
            }
            viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        }
        mText = inpText
    }

    fun generateFormattedSequence(mText: String): String {
        val words = mText.split(" ").toTypedArray()
        val viewWidth = measuredWidth
        val finalSequence = StringBuilder()

        for (word in words) {
            val temp = finalSequence.substring(
                Math.max(finalSequence.lastIndexOf("\n"), 0),
            ) + " " + word
            val textWidth = paint.measureText(temp)
            if (textWidth >= viewWidth) {
                finalSequence.append("\n").append(word)
            } else if (finalSequence.isEmpty()) {
                finalSequence.append(word)
            } else {
                finalSequence.append(" ").append(word)
            }
        }
        return finalSequence.toString()
    }

    private fun pingAnimationEnded() {
        mAnimationChangeListener?.onAnimationEnd()
    }

    fun avoidTextOverflowAtEdge(avoidTextOverflowAtEdge: Boolean) {
        this.avoidTextOverflowAtEdge = avoidTextOverflowAtEdge
    }

    fun interface OnAnimationChangeListener {
        fun onAnimationEnd()
    }

    fun setOnAnimationChangeListener(onAnimationChangeListener: OnAnimationChangeListener?) {
        mAnimationChangeListener = onAnimationChangeListener
    }
}
