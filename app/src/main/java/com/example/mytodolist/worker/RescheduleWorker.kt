package com.example.mytodolist.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mytodolist.data.repository.NotificationTimeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RescheduleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationTimeRepository: NotificationTimeRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val times = notificationTimeRepository.getAllOnce()
        NotificationScheduler.scheduleAll(applicationContext, times)
        return Result.success()
    }
}
