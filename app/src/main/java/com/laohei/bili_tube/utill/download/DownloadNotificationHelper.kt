package com.laohei.bili_tube.utill.download

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.laohei.bili_tube.R
import com.laohei.bili_tube.core.NOTIFICATION_PERMISSION
import com.laohei.bili_tube.core.correspondence.Event
import com.laohei.bili_tube.core.correspondence.EventBus
import com.laohei.bili_tube.core.util.checkedPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DownloadNotificationHelper(private val context: Context) {
    companion object {
        private val TAG = DownloadNotificationHelper::class.simpleName
    }

    private val channelId = "download_channel"
    private val groupKey = "com.laohei.bili_tube.DOWNLOAD_GROUP"
    private val summaryNotificationId = 1000

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.str_download_notification),
                NotificationManager.IMPORTANCE_LOW
            )
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showDownloadNotification(taskId: Int, title: String, progress: Int) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(context.getString(R.string.str_download_progress, progress))
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setGroup(groupKey)

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    context.checkedPermission(Manifest.permission.POST_NOTIFICATIONS).not() -> {
                CoroutineScope(Dispatchers.Default).launch {
                    EventBus.send(Event.AppEvent.PermissionRequestEvent(NOTIFICATION_PERMISSION))
                }
                return
            }
        }

        NotificationManagerCompat.from(context).notify(taskId, builder.build())
        showSummaryNotification()
    }

    fun showCompletedNotification(taskId: Int, title: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(context.getString(R.string.str_download_completed))
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .setGroup(groupKey)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    context.checkedPermission(Manifest.permission.POST_NOTIFICATIONS).not() -> {
                CoroutineScope(Dispatchers.Default).launch {
                    EventBus.send(Event.AppEvent.PermissionRequestEvent(NOTIFICATION_PERMISSION))
                }
                return
            }
        }
        NotificationManagerCompat.from(context).notify(taskId, builder.build())
        showSummaryNotification()
    }

    private fun showSummaryNotification() {
        val summaryNotification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.str_downlaod_management))
            .setContentText(context.getString(R.string.str_downloading_multiple_files))
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setGroup(groupKey)
            .setGroupSummary(true)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    context.checkedPermission(Manifest.permission.POST_NOTIFICATIONS).not() -> {
                CoroutineScope(Dispatchers.Default).launch {
                    EventBus.send(Event.AppEvent.PermissionRequestEvent(NOTIFICATION_PERMISSION))
                }
                return
            }
        }
        NotificationManagerCompat.from(context)
            .notify(summaryNotificationId, summaryNotification.build())
    }
}
