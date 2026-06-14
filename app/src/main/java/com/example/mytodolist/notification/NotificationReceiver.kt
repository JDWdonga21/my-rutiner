package com.example.mytodolist.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.mytodolist.data.db.entity.NotificationTimeEntity
import com.example.mytodolist.worker.NotificationScheduler
import com.example.mytodolist.worker.NotificationWorker

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val timeId = intent.getLongExtra("time_id", -1L)
        val hour = intent.getIntExtra("hour", -1)
        val minute = intent.getIntExtra("minute", -1)

        // 즉시 실행 OneTimeWorkRequest로 알림 발송 (Hilt 의존성 주입 필요)
        val work = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        WorkManager.getInstance(context).enqueue(work)

        // 다음 날 같은 시간에 AlarmManager 재등록
        if (timeId != -1L && hour != -1 && minute != -1) {
            NotificationScheduler.scheduleOne(
                context,
                NotificationTimeEntity(id = timeId, hour = hour, minute = minute, isEnabled = true)
            )
        }
    }
}
