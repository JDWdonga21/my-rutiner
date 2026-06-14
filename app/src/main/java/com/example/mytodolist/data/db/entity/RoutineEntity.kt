package com.example.mytodolist.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val isActive: Boolean = true,
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
