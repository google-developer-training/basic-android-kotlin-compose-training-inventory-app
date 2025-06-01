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

package com.example.inventory.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.inventory.data.Item
import java.text.NumberFormat

/**
 * ViewModel to validate and insert items in the Room database.
 * ViewModel для проверки и вставки элементов в базу данных комнат.
 */
class ItemEntryViewModel : ViewModel() {

    /**
     * Holds current item ui state
     * Сохраняет текущее состояние пользовательского интерфейса элемента
     */
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     *
     * Обновляет на значение, указанное в аргументе. Этот метод также запускает
     * проверку для входных значений.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && amount.isNotBlank() && description.isNotBlank()
        }
    }
}

/**
 * Represents Ui State for an Item.
 * Представляет состояние пользовательского интерфейса для элемента.
 */
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
)

data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val amount: String = "",
    val description: String = "",
)

/**
 * Дополнительная функция для преобразования  [ItemDetails] в [Item].
 * Если значение параметра [ItemDetails.price] равно
 * недопустимому [Double], то цена будет установлена равной 0.0.
 * Аналогично, если значение параметра [ItemDetails.amount] не является допустимым значением [Int],
 * тогда для значения amount будет установлено значение 0
 */
fun ItemDetails.toItem(): Item = Item(
    id = id,
    name = name,
    amount = amount.toIntOrNull() ?: 0,
    description = description.takeIf { it is String && it.toIntOrNull() == null } ?: "описание"
)

fun Item.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(amount)
}

/**
 * Extension function to convert [Item] to [ItemUiState]
 * Функция расширения для преобразования  [Item] в [ItemUiState]
 */
fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Item] to [ItemDetails]
 */
fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name,
    amount = amount.toString(),
    description = description.toString()
)
