package com.example.mytodolist.data.repository

import com.example.mytodolist.data.db.dao.RoutineDao
import com.example.mytodolist.data.db.entity.RoutineEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineRepository @Inject constructor(
    private val dao: RoutineDao
) {
    fun getAll(): Flow<List<RoutineEntity>> = dao.getAll()

    suspend fun getActiveRoutines(): List<RoutineEntity> = dao.getActiveRoutines()

    suspend fun add(title: String) {
        dao.insert(RoutineEntity(title = title))
    }

    suspend fun update(routine: RoutineEntity) = dao.update(routine)

    suspend fun delete(routine: RoutineEntity) = dao.delete(routine)

    suspend fun toggleActive(routine: RoutineEntity) {
        dao.update(routine.copy(isActive = !routine.isActive))
    }
}
