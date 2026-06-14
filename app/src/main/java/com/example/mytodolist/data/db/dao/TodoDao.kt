package com.example.mytodolist.data.db.dao

import androidx.room.*
import com.example.mytodolist.data.db.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

data class CompletionStats(
    val date: String,
    val completed: Int,
    val total: Int
)

@Dao
interface TodoDao {

    @Query("SELECT * FROM todos WHERE date = :date ORDER BY isOneTime ASC, id ASC")
    fun getTodosByDate(date: String): Flow<List<TodoEntity>>

    @Query("SELECT COUNT(*) FROM todos WHERE date = :date")
    suspend fun getCountByDate(date: String): Int

    @Query("SELECT * FROM todos WHERE date = :date AND isCompleted = 0")
    suspend fun getIncompleteTodosForDate(date: String): List<TodoEntity>

    @Query("""
        SELECT date,
               SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) AS completed,
               COUNT(*) AS total
        FROM todos
        WHERE date >= :fromDate
        GROUP BY date
        ORDER BY date DESC
    """)
    fun getCompletionStatsSince(fromDate: String): Flow<List<CompletionStats>>

    // 오늘 이미 등록된 루틴 ID 목록 (Fix 1: 신규 루틴만 추가하기 위해)
    @Query("SELECT routineId FROM todos WHERE date = :date AND routineId IS NOT NULL")
    suspend fun getRoutineIdsForDate(date: String): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(todos: List<TodoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoEntity): Long

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)
}
