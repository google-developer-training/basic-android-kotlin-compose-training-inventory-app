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

package com.example.inventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.inventory.data.PurchaseDetails
import com.example.inventory.ui.ConfirmationPage.PurchaseConfirmationScreen
import com.example.inventory.ui.home.HomeDestination
import com.example.inventory.ui.home.HomeScreen
import com.example.inventory.ui.item.ItemDetailsDestination
import com.example.inventory.ui.item.ItemDetailsScreen
import com.example.inventory.ui.item.ItemEditDestination
import com.example.inventory.ui.item.ItemEditScreen
import com.example.inventory.ui.item.ItemEntryDestination
import com.example.inventory.ui.item.ItemEntryScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navController = navController, // Pass NavController here
                navigateToItemEntry = { navController.navigate(ItemEntryDestination.route) },
                navigateToItemUpdate = { itemId ->
                    navController.navigate("${ItemDetailsDestination.route}/$itemId")
                }
            )
        }
        composable(route = ItemEntryDestination.route) {
            ItemEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = ItemDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ItemDetailsDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            // Pass navController to ItemDetailsScreen
            ItemDetailsScreen(
                navigateToEditItem = { itemId ->
                    navController.navigate("${ItemEditDestination.route}/$itemId")
                },
                navigateBack = { navController.navigateUp() },
                navController = navController // Add this line
            )
        }
        composable(route = "purchaseConfirmation/{productName}/{pricePerItem}/{quantityOrdered}/{totalCost}/{itemsLeftInInventory}",
            arguments = listOf(
                navArgument("productName") { type = NavType.StringType },
                navArgument("pricePerItem") { type = NavType.StringType },
                navArgument("quantityOrdered") { type = NavType.IntType },
                navArgument("totalCost") { type = NavType.StringType },
                navArgument("itemsLeftInInventory") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            PurchaseConfirmationScreen(
                navController = navController,
                purchaseDetails = PurchaseDetails(
                    productName = backStackEntry.arguments?.getString("productName") ?: "",
                    pricePerItem = backStackEntry.arguments?.getString("pricePerItem") ?: "",
                    quantityOrdered = backStackEntry.arguments?.getInt("quantityOrdered") ?: 0,
                    totalCost = backStackEntry.arguments?.getString("totalCost") ?: "",
                    itemsLeftInInventory = backStackEntry.arguments?.getInt("itemsLeftInInventory") ?: 0
                )
            )
        }
    }
}
