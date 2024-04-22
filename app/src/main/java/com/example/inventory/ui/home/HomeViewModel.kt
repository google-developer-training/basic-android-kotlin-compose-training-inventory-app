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

package com.example.inventory.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemsRepository
import kotlinx.coroutines.flow.*

/**
 * ViewModel to retrieve and manage items in the inventory, including search functionality.
 */
class HomeViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    /**
     * Holds home ui state. The list of items are retrieved from [ItemsRepository] and filtered based on
     * the current search query.
     */
    val homeUiState: StateFlow<HomeUiState> = _searchQuery
        .debounce(300)  // Debounce to limit updates
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                itemsRepository.getAllItemsStream()
            } else {
                itemsRepository.getAllItemsStream().map { items ->
                    items.filter { it.name.contains(query, ignoreCase = true) || it.id.toString() == query }
                }
            }
        }
        .map { HomeUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = HomeUiState()
        )

    /**
     * Updates the current search query.
     */
    fun searchProduct(query: String) {
        _searchQuery.value = query
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for HomeScreen.
 */
data class HomeUiState(val itemList: List<Item> = listOf())
