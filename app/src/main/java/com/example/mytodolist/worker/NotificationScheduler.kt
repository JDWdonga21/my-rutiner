package com.example.mytodolist.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.mytodolist.data.db.entity.NotificationTimeEntity
import com.example.mytodolist.notification.NotificationReceiver
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object NotificationScheduler {

    fun scheduleAll(context: Context, times: List<NotificationTimeEntity>) {
        times.forEach { time ->
            if (time.isEnabled) scheduleOne(context, time)
            else cancelOne(context, time.id)
        }
    }

    fun scheduleOne(context: Context, time: NotificationTimeEntity) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        // Doze 모드에서도 정확하게 실행하려면 SCHEDULE_EXACT_ALARM 권한 필요
        // API 31+ 에서 권한 없을 경우 setAndAllowWhileIdle로 폴백 (수분 이내 오차)
        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()

        val triggerMs = nextTriggerMillis(time.hour, time.minute)
        val pendingIntent = buildPendingIntent(context, time)

        if (canExact) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, triggerMs, pendingIntent
            )
        }
    }

    fun cancelOne(context: Context, id: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, NotificationReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, id.toInt(), intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pi?.let { alarmManager.cancel(it) }
    }

    private fun buildPendingIntent(context: Context, time: NotificationTimeEntity): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("time_id", time.id)
            putExtra("hour", time.hour)
            putExtra("minute", time.minute)
        }
        return PendingIntent.getBroadcast(
            context, time.id.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun nextTriggerMillis(hour: Int, minute: Int): Long {
        val now = java.time.LocalDateTime.now()
        var target = LocalDate.now().atTime(LocalTime.of(hour, minute))
        if (!target.isAfter(now)) target = target.plusDays(1)
        return target.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
