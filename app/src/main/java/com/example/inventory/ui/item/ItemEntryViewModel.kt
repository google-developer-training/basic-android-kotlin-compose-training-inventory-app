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
import com.example.inventory.data.ItemsRepository
import java.text.NumberFormat

/**
 * ViewModel для проверки и вставки элементов в базу данных Room.<br> <br><br>
 *
 * Функция расширения ItemDetails.toItem() преобразует объект состояния ItemUiState пользовательского интерфейса в тип сущности Item.<br>
 * Функция расширения Item.toItemUiState() преобразует объект Item Room в тип состояния ItemUiState UI.<br>
 * Функция расширения Item.toItemDetails() преобразует объект Item Room в ItemDetails.
 */

class ItemEntryViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {

    /**
     * Сохраняет текущее состояние пользовательского интерфейса элемента
     */
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    /**
     * Обновляет [itemUiState] значением, указанным в аргументе. Этот метод также запускает
     * проверку для входных значений.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    /**
     * Проверяет, не пусты ли поля name, price, и quantity. <br><br>
     *
     * Тут детали:
     * <a href="https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room?continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-6-pathway-2%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-persisting-data-room#8">Добавьте функцию сохранения</a>
     *
     */
    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && amount.isNotBlank()
        }
    }

    suspend fun saveItem() {
        if (validateInput()) {
            // Здесь сохраняете itemDetails в базу данных
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }

}

/**
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
    description = description
)

fun Item.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(amount)
}

/**
 * Функция расширения для преобразования  [Item] в [ItemUiState].
 */
fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

/**
 *Функция расширения для преобразования [Item] в [ItemDetails]
 *  */
fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name,
    amount = amount.toString(),
    description = description.toString()
)
