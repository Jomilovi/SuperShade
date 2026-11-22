package com.supershade.service

import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.supershade.domain.notification.NotificationRepository
import org.koin.android.ext.android.inject
import java.util.concurrent.ConcurrentHashMap

/**
 * Listens for posted/removed notifications from the system and forwards them
 * to [NotificationRepository], which owns the authoritative in-memory list.
 *
 * The service must be enabled by the user via Settings → Notification Access.
 * Once connected, [onListenerConnected] replays all currently active notifications
 * so the shade starts fully populated.
 */
class NotificationCollector : NotificationListenerService() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val pendingCancellationChecks = ConcurrentHashMap.newKeySet<String>()

    companion object {
        @Volatile var instance: NotificationCollector? = null
            private set
    }

    private val repository: NotificationRepository by inject()

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        repository.onNotificationPosted(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        pendingCancellationChecks.remove(sbn.key)
        repository.onNotificationRemoved(sbn.key)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        repository.canceller = ::requestNotificationCancellation
        repository.snoozer = { key, durationMs ->
            try {
                snoozeNotification(key, durationMs)
            } catch (_: Exception) {}
        }
        refreshNotifications()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        instance = null
        mainHandler.removeCallbacksAndMessages(null)
        pendingCancellationChecks.clear()
        repository.clearPendingDismissals()
        repository.canceller = null
        repository.snoozer = null
    }

    private fun requestNotificationCancellation(key: String): Boolean {
        if (!pendingCancellationChecks.add(key)) return true
        return try {
            cancelNotification(key)
            mainHandler.postDelayed({ verifyNotificationCancellation(key, retry = 0) }, 350L)
            true
        } catch (error: Exception) {
            pendingCancellationChecks.remove(key)
            Log.w("NotificationCollector", "Could not dismiss notification", error)
            false
        }
    }

    private fun verifyNotificationCancellation(key: String, retry: Int) {
        if (key !in pendingCancellationChecks) return

        val activeNotificationsSnapshot = try {
            activeNotifications
        } catch (error: Exception) {
            Log.w("NotificationCollector", "Could not verify notification dismissal", error)
            null
        }

        if (activeNotificationsSnapshot == null) {
            if (retry == 0) {
                mainHandler.postDelayed({ verifyNotificationCancellation(key, retry = 1) }, 350L)
            } else {
                pendingCancellationChecks.remove(key)
                repository.releasePendingDismissal(key)
                repository.refresh()
            }
            return
        }

        val active = activeNotificationsSnapshot.firstOrNull { it.key == key }
        if (active == null) {
            pendingCancellationChecks.remove(key)
            repository.onNotificationRemoved(key)
            return
        }

        if (retry == 0) {
            try {
                cancelNotification(key)
            } catch (error: Exception) {
                Log.w("NotificationCollector", "Retrying notification dismissal failed", error)
            }
            mainHandler.postDelayed({ verifyNotificationCancellation(key, retry = 1) }, 350L)
        } else {
            pendingCancellationChecks.remove(key)
            repository.restoreFailedDismissal(active)
        }
    }

    fun refreshNotifications() {
        try {
            val current = activeNotifications ?: return
            current.forEach { repository.onNotificationPosted(it) }
        } catch (_: Exception) {}
    }
}
