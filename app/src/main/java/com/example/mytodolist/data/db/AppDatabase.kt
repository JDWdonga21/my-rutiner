package com.example.mytodolist.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mytodolist.data.db.dao.NotificationTimeDao
import com.example.mytodolist.data.db.dao.RoutineDao
import com.example.mytodolist.data.db.dao.TodoDao
import com.example.mytodolist.data.db.entity.NotificationTimeEntity
import com.example.mytodolist.data.db.entity.RoutineEntity
import com.example.mytodolist.data.db.entity.TodoEntity

@Database(
    entities = [RoutineEntity::class, TodoEntity::class, NotificationTimeEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun todoDao(): TodoDao
    abstract fun notificationTimeDao(): NotificationTimeDao
}
