package com.example.inventory.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.ItemsRepository
import kotlinx.coroutines.launch

class ItemEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    var itemUiState by mutableStateOf(ItemUiState())
        private set

    private val itemId: Int = checkNotNull(savedStateHandle[ItemEditDestination.itemIdArg])

    init {
        loadItem()
    }

    private fun loadItem() {
        viewModelScope.launch {
            itemsRepository.getItemStream(itemId)
                .collect { item ->
                    if (item != null) {
                        itemUiState = ItemUiState(
                            itemDetails = item.toItemDetails(),
                            isEntryValid = validateInput(item.toItemDetails())
                        )
                    }
                }
        }
    }

    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState = ItemUiState(
            itemDetails = itemDetails,
            isEntryValid = validateInput(itemDetails)
        )
    }

    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && amount.isNotBlank() // ← description убрано!
        }
    }

    suspend fun saveItem() {
        if (validateInput()) {
            itemsRepository.updateItem(itemUiState.itemDetails.toItem())
        }
    }
}