package com.ivy.wallet.domain.deprecated.logic.notification

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ivy.base.legacy.SharedPrefs
import com.ivy.domain.AppStarter
import com.ivy.legacy.utils.timeNowLocal
import com.ivy.legacy.utils.toEpochSeconds
import com.ivy.ui.R
import com.ivy.wallet.android.notification.IvyNotificationChannel
import com.ivy.wallet.android.notification.NotificationService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Deprecated("Use FP style, look into `domain.fp` package")
class TransactionReminderLogic @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val sharedPrefs: SharedPrefs,
    private val notificationService: NotificationService,
    private val appStarter: AppStarter,
) {
    companion object {
        private const val UNIQUE_WORK_NAME_V1 = "transaction_reminder_work"
        private const val UNIQUE_WORK_NAME_V2 = "transaction_reminder_work_v2"
        private const val UNIQUE_WORK_NAME_TEST = "transaction_reminder_work_test"
    }

    fun getReminderHour(): Int =
        sharedPrefs.getInt(SharedPrefs.REMINDER_HOUR, 20)

    fun getReminderMinute(): Int =
        sharedPrefs.getInt(SharedPrefs.REMINDER_MINUTE, 0)

    fun testNow() {
        val workBuilder = PeriodicWorkRequestBuilder<TransactionReminderWorker>(5, TimeUnit.MINUTES)

        WorkManager
            .getInstance(appContext)
            .enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME_TEST,
                ExistingPeriodicWorkPolicy.REPLACE,
                workBuilder.build()
            )
    }

    fun sendTestNotification() {
        val notification = notificationService
            .defaultIvyNotification(
                channel = IvyNotificationChannel.TRANSACTION_REMINDER,
                priority = NotificationCompat.PRIORITY_HIGH
            )
            .setContentTitle("Ivy Wallet")
            .setContentText(appContext.getString(R.string.notification_1))
            .setContentIntent(
                PendingIntent.getActivity(
                    appContext,
                    1,
                    appStarter.getRootIntent(),
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_UPDATE_CURRENT
                            or PendingIntent.FLAG_IMMUTABLE
                )
            )

        notificationService.showNotification(notification, 1)
    }

    fun scheduleReminder(
        hour: Int = getReminderHour(),
        minute: Int = getReminderMinute(),
        replace: Boolean = false
    ) {
        if (!fetchShowNotifications()) {
            cancelReminder()
            return
        }

        val timeNowLocal = timeNowLocal()
        val todayReminder = timeNowLocal()
            .withHour(hour)
            .withMinute(minute)
            .withSecond(0)

        val initialDelaySeconds = if (todayReminder.isAfter(timeNowLocal)) {
            // Reminder time is in the future today, schedule for today
            todayReminder.toEpochSeconds() - timeNowLocal.toEpochSeconds()
        } else {
            // Reminder time has passed today, schedule starting from tomorrow
            todayReminder.plusDays(1).toEpochSeconds() - timeNowLocal.toEpochSeconds()
        }

        val workBuilder = PeriodicWorkRequestBuilder<TransactionReminderWorker>(24, TimeUnit.HOURS)
        if (initialDelaySeconds > 0) {
            workBuilder.setInitialDelay(initialDelaySeconds, TimeUnit.SECONDS)
        }

        val policy = if (replace) ExistingPeriodicWorkPolicy.REPLACE else ExistingPeriodicWorkPolicy.KEEP

        WorkManager
            .getInstance(appContext)
            .enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME_V2,
                policy,
                workBuilder.build()
            )
    }

    fun cancelReminder() {
        WorkManager
            .getInstance(appContext)
            .cancelUniqueWork(UNIQUE_WORK_NAME_V2)
    }

    private fun fetchShowNotifications(): Boolean =
        sharedPrefs.getBoolean(SharedPrefs.SHOW_NOTIFICATIONS, true)
}
