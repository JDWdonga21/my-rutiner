package com.example.mytodolist.domain

import com.example.mytodolist.data.db.entity.TodoEntity
import com.example.mytodolist.data.repository.RoutineRepository
import com.example.mytodolist.data.repository.TodoRepository
import java.time.LocalDate
import javax.inject.Inject

class GenerateTodayTodosUseCase @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val todoRepository: TodoRepository
) {
    suspend operator fun invoke() {
        val today = LocalDate.now().toString()

        // 오늘 이미 등록된 루틴 ID 목록
        val existingRoutineIds = todoRepository.getRoutineIdsForDate(today).toSet()

        // 활성 루틴 중 아직 오늘 ToDo가 없는 것만 추가 (Fix 1, Fix 3)
        val activeRoutines = routineRepository.getActiveRoutines()
        val newTodos = activeRoutines
            .filter { it.id !in existingRoutineIds }
            .map { routine ->
                TodoEntity(
                    routineId = routine.id,
                    date = today,
                    title = routine.title,
                    isOneTime = false
                )
            }

        if (newTodos.isNotEmpty()) {
            todoRepository.insertAll(newTodos)
        }
    }
}
