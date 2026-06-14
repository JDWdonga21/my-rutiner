package com.example.mytodolist.worker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.mytodolist.data.repository.TodoRepository
import com.example.mytodolist.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val todoRepository: TodoRepository
) : CoroutineWorker(context, params) {

    // Expedited Work 지원: 배터리 절약 모드에서도 즉시 실행
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationHelper.buildSilentForegroundNotification(applicationContext)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ForegroundInfo(9999, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE)
        } else {
            ForegroundInfo(9999, notification)
        }
    }

    override suspend fun doWork(): Result {
        val today = LocalDate.now().toString()
        val incomplete = todoRepository.getIncompleteTodosForDate(today)
        if (incomplete.isNotEmpty()) {
            NotificationHelper.sendReminderNotification(applicationContext, incomplete.size)
        }
        return Result.success()
    }
}
