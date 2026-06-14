package com.example.mytodolist.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "todos",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("routineId"), Index("date")]
)
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: Long?,
    val date: String,           // "2026-06-14"
    val title: String,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val isOneTime: Boolean = false  // 오늘만 할 일 여부
)
