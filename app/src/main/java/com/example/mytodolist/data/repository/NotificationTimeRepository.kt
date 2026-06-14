package com.example.mytodolist.data.repository

import com.example.mytodolist.data.db.dao.NotificationTimeDao
import com.example.mytodolist.data.db.entity.NotificationTimeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationTimeRepository @Inject constructor(
    private val dao: NotificationTimeDao
) {
    fun getAll(): Flow<List<NotificationTimeEntity>> = dao.getAll()

    suspend fun getEnabledTimes(): List<NotificationTimeEntity> = dao.getEnabledTimes()

    suspend fun getAllOnce(): List<NotificationTimeEntity> = dao.getAllOnce()

    suspend fun add(hour: Int, minute: Int) {
        dao.insert(NotificationTimeEntity(hour = hour, minute = minute))
    }

    suspend fun toggleEnabled(time: NotificationTimeEntity) {
        dao.update(time.copy(isEnabled = !time.isEnabled))
    }

    suspend fun delete(time: NotificationTimeEntity) = dao.delete(time)
}
