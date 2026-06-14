package com.example.mytodolist.data.db.dao

import androidx.room.*
import com.example.mytodolist.data.db.entity.NotificationTimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationTimeDao {

    @Query("SELECT * FROM notification_times ORDER BY hour ASC, minute ASC")
    fun getAll(): Flow<List<NotificationTimeEntity>>

    @Query("SELECT * FROM notification_times ORDER BY hour ASC, minute ASC")
    suspend fun getAllOnce(): List<NotificationTimeEntity>

    @Query("SELECT * FROM notification_times WHERE isEnabled = 1")
    suspend fun getEnabledTimes(): List<NotificationTimeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(time: NotificationTimeEntity): Long

    @Update
    suspend fun update(time: NotificationTimeEntity)

    @Delete
    suspend fun delete(time: NotificationTimeEntity)
}
