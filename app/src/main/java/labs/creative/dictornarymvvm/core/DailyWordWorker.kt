package labs.creative.dictornarymvvm.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import labs.creative.dictornarymvvmapp.R
import java.time.LocalDate

/**
 * Background worker that picks a "word of the day" and posts a local
 * notification. This is a plain [CoroutineWorker] (not Hilt-injected) since
 * the project does not currently depend on `androidx.hilt:hilt-work`. The
 * word pool below intentionally mirrors the small lookup used by
 * `MainViewModel`'s word-of-the-day feature rather than sharing it, to keep
 * this worker self-contained.
 */
class DailyWordWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val NOTIFICATION_ID = 2001

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
    }

    override suspend fun doWork(): Result {
        val dayOfYear = LocalDate.now().dayOfYear
        val word = wordOfTheDayPool[dayOfYear % wordOfTheDayPool.size]

        showNotification(word)

        return Result.success()
    }

    private fun showNotification(word: String) {
        val context = applicationContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val notification = NotificationCompat.Builder(context, NotificationChannels.DAILY_WORD_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_search)
            .setContentTitle(context.getString(R.string.daily_word_notification_title))
            .setContentText(word.replaceFirstChar { it.uppercase() })
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
