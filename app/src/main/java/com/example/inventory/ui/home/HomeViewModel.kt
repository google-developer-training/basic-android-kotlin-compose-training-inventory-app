package com.example.inventory.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Task
import com.example.inventory.data.TasksRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: TasksRepository) : ViewModel() {
    val homeUiState: StateFlow<HomeUiState> = repository.getAllTasksStream()
        .map(::HomeUiState)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun delete(task: Task) = viewModelScope.launch { repository.deleteTask(task) }
    fun insert(task: Task) = viewModelScope.launch { repository.insertTask(task) }
}

data class HomeUiState(val taskList: List<Task> = emptyList())