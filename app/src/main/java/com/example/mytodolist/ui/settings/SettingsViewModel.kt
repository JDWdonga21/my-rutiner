package com.example.mytodolist.ui.settings

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodolist.data.db.entity.NotificationTimeEntity
import com.example.mytodolist.data.repository.NotificationTimeRepository
import com.example.mytodolist.data.repository.TodoRepository
import com.example.mytodolist.notification.NotificationHelper
import com.example.mytodolist.worker.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val notificationTimeRepository: NotificationTimeRepository,
    private val todoRepository: TodoRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val canScheduleExactAlarms: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()

    fun openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    val notificationTimes: StateFlow<List<NotificationTimeEntity>> =
        notificationTimeRepository.getAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            notificationTimeRepository.add(hour, minute)
            rescheduleAll()
        }
    }

    fun toggleTime(time: NotificationTimeEntity) {
        viewModelScope.launch {
            notificationTimeRepository.toggleEnabled(time)
            rescheduleAll()
        }
    }

    fun deleteTime(time: NotificationTimeEntity) {
        viewModelScope.launch {
            NotificationScheduler.cancelOne(context, time.id)
            notificationTimeRepository.delete(time)
        }
    }

    fun sendTestNotification() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val incomplete = todoRepository.getIncompleteTodosForDate(today)
            if (incomplete.isNotEmpty()) {
                NotificationHelper.sendReminderNotification(context, incomplete.size)
            } else {
                // 미완료 항목 없어도 테스트용으로 1개로 발송
                NotificationHelper.sendReminderNotification(context, 0)
            }
        }
    }

    private suspend fun rescheduleAll() {
        val times = notificationTimeRepository.getAllOnce()
        NotificationScheduler.scheduleAll(context, times)
    }
}
