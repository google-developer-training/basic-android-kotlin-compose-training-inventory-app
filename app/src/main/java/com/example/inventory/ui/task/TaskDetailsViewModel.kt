package com.example.inventory.ui.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.TasksRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve, update and delete a task from the [TasksRepository]'s data source.
 */
class TaskDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    // 從 navigation arguments 拿到要顯示的 taskId
    private val taskId: Int = checkNotNull(savedStateHandle[TaskDetailsDestination.taskIdArg])

    /**
     * uiState 會隨著資料庫中該筆 task 更新自動推送最新值
     */
    val uiState: StateFlow<TaskDetailsUiState> =
        tasksRepository
            .getTaskStream(taskId)                // 回傳 Flow<TaskEntity?>
            .filterNotNull()                      // 避免 null
            .map { entity ->
                // Map Entity -> UI state
                TaskDetailsUiState(taskDetails = entity.toItemDetails())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TaskDetailsUiState()
            )

    /**
     * 刪除該 task
     */
    fun deleteTask() {
        viewModelScope.launch {
            tasksRepository.deleteTask(uiState.value.taskDetails.toTask())
        }
    }

    /**
     * 更新該 task 的 name 與 priority
     */
    fun updateTask(name: String, priority: String) {
        viewModelScope.launch {
            // 構造一個新的 TaskData 並交給 repository 更新
            val updated = uiState.value.taskDetails.copy(name = name, priority = priority)
            tasksRepository.updateTask(updated.toTask())
            // 不需要手動刷新，getTaskStream 會自動 emit 最新的資料
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * UI state for TaskDetailsScreen
 */
data class TaskDetailsUiState(
    val taskDetails: TaskDetails = TaskDetails()
)