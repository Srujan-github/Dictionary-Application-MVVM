package labs.creative.dictornarymvvm.core

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import labs.creative.dictornarymvvmapp.R

/**
 * Central place to register notification channels used by the app.
 * Safe to call multiple times (e.g. on every process start) since
 * [NotificationManager.createNotificationChannel] is idempotent.
 */
object NotificationChannels {

    const val DAILY_WORD_CHANNEL_ID = "daily_word_channel"

    fun createDailyWordChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            DAILY_WORD_CHANNEL_ID,
            context.getString(R.string.daily_word_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.daily_word_notification_channel_desc)
        }

        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }
}
