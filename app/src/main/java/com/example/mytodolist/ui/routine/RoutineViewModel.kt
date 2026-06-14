package com.example.mytodolist.ui.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodolist.data.db.entity.RoutineEntity
import com.example.mytodolist.data.repository.RoutineRepository
import com.example.mytodolist.domain.GenerateTodayTodosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val repository: RoutineRepository,
    private val generateTodayTodos: GenerateTodayTodosUseCase
) : ViewModel() {

    val routines: StateFlow<List<RoutineEntity>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRoutine(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.add(title.trim())
            // Fix 1: 루틴 추가 즉시 오늘 ToDo에 반영
            generateTodayTodos()
        }
    }

    fun toggleActive(routine: RoutineEntity) {
        viewModelScope.launch { repository.toggleActive(routine) }
    }

    fun delete(routine: RoutineEntity) {
        // Fix 3: 루틴 삭제 → 오늘 ToDo는 유지 (DB FK SET_NULL 처리됨)
        // GenerateTodayTodosUseCase가 삭제된 루틴 ID는 재추가하지 않음
        viewModelScope.launch { repository.delete(routine) }
    }
}
