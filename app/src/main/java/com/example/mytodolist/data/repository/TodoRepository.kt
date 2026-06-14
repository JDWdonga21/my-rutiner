package com.example.mytodolist.data.repository

import com.example.mytodolist.data.db.dao.CompletionStats
import com.example.mytodolist.data.db.dao.TodoDao
import com.example.mytodolist.data.db.entity.TodoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val dao: TodoDao
) {
    fun getTodosByDate(date: String): Flow<List<TodoEntity>> =
        dao.getTodosByDate(date)

    suspend fun getCountByDate(date: String): Int =
        dao.getCountByDate(date)

    suspend fun getIncompleteTodosForDate(date: String): List<TodoEntity> =
        dao.getIncompleteTodosForDate(date)

    fun getCompletionStatsSince(fromDate: String): Flow<List<CompletionStats>> =
        dao.getCompletionStatsSince(fromDate)

    suspend fun getRoutineIdsForDate(date: String): List<Long> =
        dao.getRoutineIdsForDate(date)

    suspend fun insertAll(todos: List<TodoEntity>) =
        dao.insertAll(todos)

    suspend fun addOneTimeTodo(date: String, title: String) {
        dao.insert(
            TodoEntity(routineId = null, date = date, title = title, isOneTime = true)
        )
    }

    suspend fun update(todo: TodoEntity) =
        dao.update(todo)

    suspend fun toggleComplete(todo: TodoEntity) {
        val now = if (!todo.isCompleted) System.currentTimeMillis() else null
        dao.update(todo.copy(isCompleted = !todo.isCompleted, completedAt = now))
    }

    suspend fun delete(todo: TodoEntity) =
        dao.delete(todo)
}
