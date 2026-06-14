package com.example.mytodolist.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.mytodolist.MainActivity
import com.example.mytodolist.R

object NotificationHelper {

    const val CHANNEL_ID = "todo_reminder"
    private const val NOTIFICATION_ID = 1001

    private const val FOREGROUND_CHANNEL_ID = "todo_worker"

    fun createChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)

        // 알림 채널
        val alarmChannel = NotificationChannel(
            CHANNEL_ID,
            "루틴 알림",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "미완료 루틴 알림"
            enableVibration(true)
        }
        // Expedited Worker용 포그라운드 채널 (소리 없음)
        val workerChannel = NotificationChannel(
            FOREGROUND_CHANNEL_ID,
            "백그라운드 작업",
            NotificationManager.IMPORTANCE_LOW
        )
        manager.createNotificationChannel(alarmChannel)
        manager.createNotificationChannel(workerChannel)
    }

    fun buildSilentForegroundNotification(context: Context): Notification =
        NotificationCompat.Builder(context, FOREGROUND_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("루틴 확인 중...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

    fun canNotify(context: Context): Boolean {
        // Android 13+ 런타임 권한 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) return false
        }
        // 채널 또는 앱 알림 자체가 꺼진 경우
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    fun sendReminderNotification(context: Context, incompleteCount: Int) {
        if (!canNotify(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val bodyText = if (incompleteCount > 0)
            "아직 ${incompleteCount}개의 항목이 남아있어요."
        else
            "알림 테스트입니다."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("오늘 루틴을 완료하세요!")
            .setContentText(bodyText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
