/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.inventory.data.Task
import com.example.inventory.data.TasksRepository
import java.text.NumberFormat

/**
 * ViewModel to validate and insert items in the Room database.
 */
class ItemEntryViewModel(private val tasksRepository: TasksRepository) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(taskDetails: TaskDetails) {
        itemUiState =
            ItemUiState(taskDetails = taskDetails, isEntryValid = validateInput(taskDetails))
    }

    /**
     * Inserts an [Task] in the Room database
     */
    suspend fun saveItem() {
        if (validateInput()) {
            tasksRepository.insertTask(itemUiState.taskDetails.toTask())
        }
    }

    private fun validateInput(uiState: TaskDetails = itemUiState.taskDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && priority.isNotBlank()
        }
    }
}

/**
 * Represents Ui State for an Item.
 */
data class ItemUiState(
    val taskDetails: TaskDetails = TaskDetails(),
    val isEntryValid: Boolean = false
)

data class TaskDetails(
    val id: Int = 0,
    val name: String = "",
    val priority: String = "",
)

/**
 * Extension function to convert [ItemUiState] to [Task]. If the value of [TaskDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemUiState] is not a valid [Int], then the quantity will be set to 0
 */
fun TaskDetails.toTask(): Task = Task(
    id = id,
    name = name,
    priority = priority
)


/**
 * Extension function to convert [Task] to [ItemUiState]
 */
fun Task.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    taskDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Task] to [TaskDetails]
 */
fun Task.toItemDetails(): TaskDetails = TaskDetails(
    id = id,
    name = name,
    priority = priority
)
