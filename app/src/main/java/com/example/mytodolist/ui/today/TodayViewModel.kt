package com.example.mytodolist.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodolist.data.db.entity.TodoEntity
import com.example.mytodolist.data.repository.TodoRepository
import com.example.mytodolist.domain.GenerateTodayTodosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TodayUiState(
    val date: String = "",
    val routineTodos: List<TodoEntity> = emptyList(),
    val oneTimeTodos: List<TodoEntity> = emptyList(),
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val completionRate: Float = 0f
)

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val generateTodayTodos: GenerateTodayTodosUseCase
) : ViewModel() {

    val today: String = LocalDate.now().toString()

    val uiState: StateFlow<TodayUiState> = todoRepository
        .getTodosByDate(today)
        .map { todos ->
            val routines = todos.filter { !it.isOneTime }
            val onetime = todos.filter { it.isOneTime }
            val completed = todos.count { it.isCompleted }
            val total = todos.size
            TodayUiState(
                date = today,
                routineTodos = routines,
                oneTimeTodos = onetime,
                completedCount = completed,
                totalCount = total,
                completionRate = if (total > 0) completed.toFloat() / total else 0f
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodayUiState(date = today))

    init {
        viewModelScope.launch { generateTodayTodos() }
    }

    fun toggleTodo(todo: TodoEntity) {
        viewModelScope.launch { todoRepository.toggleComplete(todo) }
    }

    fun addOneTimeTodo(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch { todoRepository.addOneTimeTodo(today, title.trim()) }
    }

    fun deleteOneTimeTodo(todo: TodoEntity) {
        viewModelScope.launch { todoRepository.delete(todo) }
    }
}
