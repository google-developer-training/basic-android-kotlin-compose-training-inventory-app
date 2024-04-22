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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.data.Item
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.item.formatedPrice
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.InventoryTheme

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    navigateToItemEntry: () -> Unit,
    navigateToItemUpdate: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val searchTextState = remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            InventoryTopAppBar(
                title = stringResource(id = R.string.app_name),
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToItemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(end = WindowInsets.safeDrawing.asPaddingValues().calculateEndPadding(LocalLayoutDirection.current))
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.item_entry_title))
            }
        },
    ) { innerPadding ->
        HomeBody(
            itemList = homeUiState.itemList,
            onItemClick = navigateToItemUpdate,
            searchTextState = searchTextState,
            onSearchTriggered = { searchText -> viewModel.searchProduct(searchText) },
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp)
        )
    }
}

@Composable
private fun HomeBody(
    itemList: List<Item>,
    onItemClick: (Int) -> Unit,
    searchTextState: MutableState<String>,
    onSearchTriggered: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    Column(modifier = modifier.padding(contentPadding)) {
        OutlinedTextField(
            value = searchTextState.value,
            onValueChange = {
                searchTextState.value = it
                onSearchTriggered(it)
            },
            label = { Text("Search by ID or Name") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { onSearchTriggered(searchTextState.value) }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearchTriggered(searchTextState.value)
            }),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        if (itemList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_item_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                items(items = itemList, key = { it.id }) { item ->
                    InventoryItem(item = item, modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { onItemClick(item.id) })
                }
            }
        }
    }
}

@Composable
private fun InventoryItem(
    item: Item, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = item.formatedPrice(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = stringResource(R.string.in_stock, item.quantity),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    val mockItems = listOf(
        Item(1, "Game", 100.0, 20),
        Item(2, "Pen", 200.0, 30),
        Item(3, "TV", 300.0, 50)
    )
    val searchTextState = remember { mutableStateOf("") }
    InventoryTheme {
        HomeBody(
            itemList = mockItems,
            onItemClick = {},  // You might need to handle navigation or actions in real previews
            searchTextState = searchTextState,
            onSearchTriggered = {},
            contentPadding = PaddingValues(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview() {
    val searchTextState = remember { mutableStateOf("") }
    InventoryTheme {
        HomeBody(
            itemList = listOf(),
            onItemClick = {},  // No navigation handling necessary for preview
            searchTextState = searchTextState,
            onSearchTriggered = {},
            contentPadding = PaddingValues(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InventoryItemPreview() {
    InventoryTheme {
        InventoryItem(
            Item(1, "Game", 100.0, 20),
        )
    }
}

