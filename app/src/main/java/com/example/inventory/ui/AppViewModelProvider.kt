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

package com.example.inventory.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.inventory.InventoryApplication
import com.example.inventory.ui.home.HomeViewModel
import com.example.inventory.ui.item.ItemDetailsViewModel
import com.example.inventory.ui.item.ItemEditViewModel
import com.example.inventory.ui.item.ItemEntryViewModel

/**
 * Предоставляет фабрику для создания экземпляра ViewModel для всего приложения Inventory
 *
 * > "В этом коде:
 * > - *Абстрактный продукт (интерфейс)* — ViewModel, из пакета androidx.lifecycle
 * > - *Конкретные продукты* — HomeViewModel, ItemEntryViewModel, ItemEditViewModel, ItemDetailsViewModel,
 * > - *Конкретные создатели* — каждый initializer { ... } с лямбдой,
 * > - *Абстрактный создатель* — неявно представлен самой идеей фабрики viewModelFactory
 *
 * Он эти вьюхи пачкой создаёт?
 *
 * нет, не пачкой а по одной, тот который запрашивает в конкретном активити/фрагменте,
 *
 * он сохраняется в ViewModelStore и будет жить до уничтожения активити/фрагмента
 *
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Инициализатор для ItemEditViewModel
        initializer {
            ItemEditViewModel(
                this.createSavedStateHandle()
            )
        }
        // Инициализатор для ItemEntryViewModel
        initializer {
            ItemEntryViewModel(inventoryApplication().container.itemsRepository)
        }

        // Инициализатор для ItemDetailsViewModel
// Инициализатор для ItemDetailsViewModel
        initializer {
            ItemDetailsViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                itemsRepository = inventoryApplication().container.itemsRepository
            )
        }

        // Инициализатор для HomeViewModel
        initializer {
            HomeViewModel(inventoryApplication().container.itemsRepository)
        }
    }
}

/**
 * Функция расширения запрашивает объект [Application] и возвращает экземпляр
 * [InventoryApplication].
 */
fun CreationExtras.inventoryApplication(): InventoryApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)
