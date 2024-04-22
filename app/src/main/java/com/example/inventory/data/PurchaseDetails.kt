package com.example.inventory.data

data class PurchaseDetails(
    val productName: String,
    val pricePerItem: String,
    val quantityOrdered: Int,
    val totalCost: String,
    val itemsLeftInInventory: Int
)
