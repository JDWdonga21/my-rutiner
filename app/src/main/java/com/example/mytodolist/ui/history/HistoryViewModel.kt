package com.example.mytodolist.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodolist.data.db.dao.CompletionStats
import com.example.mytodolist.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    repository: TodoRepository
) : ViewModel() {

    private val fromDate = LocalDate.now().minusDays(6).toString()

    val weeklyStats: StateFlow<List<CompletionStats>> = repository
        .getCompletionStatsSince(fromDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
