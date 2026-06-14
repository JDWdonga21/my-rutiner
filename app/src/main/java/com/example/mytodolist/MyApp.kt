package com.example.mytodolist

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.mytodolist.data.repository.NotificationTimeRepository
import com.example.mytodolist.notification.NotificationHelper
import com.example.mytodolist.worker.NotificationScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var notificationTimeRepository: NotificationTimeRepository

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        rescheduleAllAlarms()
    }

    private fun rescheduleAllAlarms() {
        CoroutineScope(Dispatchers.IO).launch {
            // 앱 시작 시 모든 알람 재등록 (앱 업데이트 후에도 AlarmManager 알람이 유지되도록)
            val times = notificationTimeRepository.getAllOnce()
            NotificationScheduler.scheduleAll(this@MyApp, times)
        }
    }
}
