/*
  Этот файл представляет собой главный экран или первый экран приложения,
который содержит компоненты для отображения списка инвентаря.
На нем есть кнопка FAB +для добавления новых элементов в список.
Элементы в списке отображаются позже на пути.
 */

package com.example.inventory.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.inventory.R
import com.example.inventory.data.Item
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.InventoryTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    navigateToItemUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToItemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.item_entry_title)
                )
            }
        },
    ) { innerPadding ->
        HomeBody(
            itemList = homeUiState.itemList,
            onItemClick = navigateToItemUpdate,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun HomeBody(
    itemList: List<Item>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (itemList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_item_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            // Рассчитываем общую сумму, бюджет и расходы
            val totalAmount = itemList.sumOf { it.amount }
            val budgetAmount = itemList.filter { it.amount > 0 }.sumOf { it.amount }
            val expenseAmount = itemList.filter { it.amount < 0 }.sumOf { it.amount }

            // Отображаем CASHFLOW
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "CASHFLOW",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "${totalAmount} ₽",
                            style = MaterialTheme.typography.displayLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Отображаем мотивационное сообщение справа от суммы кэшфлоу
                    Text(
                        text = "Не отвлекаясь по пустякам\n- достигнешь большего.\nСосредоточившись на главном -\nизменишь всё.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Отображаем BUDGET и EXPENSE
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "$budgetAmount ₽",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.Green,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "BUDGET",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "$expenseAmount ₽",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.Red,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = "EXPENSE",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Отображаем TRANSACTIONS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "TRANSACTIONS",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                InventoryList(
                    itemList = itemList,
                    onItemClick = { onItemClick(it.id) },
                    contentPadding = contentPadding,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun InventoryList(
    itemList: List<Item>,
    onItemClick: (Item) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(items = itemList, key = { it.id }) { item ->
            TransactionItem(
                item = item,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onItemClick(item) }
            )
        }
    }
}

@Composable
private fun TransactionItem(
    item: Item, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        val amountText = if (item.amount >= 0) "+${item.amount}" else "${item.amount}"
        val amountColor = if (item.amount >= 0) Color.Green else Color.Red

        Text(
            text = "$amountText ₽",
            style = MaterialTheme.typography.bodyMedium,
            color = amountColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    InventoryTheme {
        HomeBody(listOf(
            Item(1, "ЗП", 68000, "Зарплата"),
            Item(2, "ЕДВ", 4000, "Ежемесячные денежные выплаты"),
            Item(3, "Авансы", 10, "Аванс"),
            Item(4, "Жильё", -27500, "Оплата жилья"),
            Item(5, "На прод 15к с зп", -15000, "Продукты"),
            Item(6, "Нюше корм 3к(6/2)", -3000, "Корм для питомца"),
            Item(7, "Дорога 1600", -2500, "Проезд"),
            Item(8, "Интернет связь кино", -10, "Развлечения"),
            Item(9, "Ипотека с аванса", -10, "Ипотека"),
            Item(10, "Коммуналка 2500", -10, "Коммунальные услуги"),
            Item(11, "Наташин кредит", -1, "Кредит"),
            Item(12, "Маме на отопление 20к", 0, "Помощь маме")
        ), onItemClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview() {
    InventoryTheme {
        HomeBody(listOf(), onItemClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionItemPreview() {
    InventoryTheme {
        TransactionItem(
            Item(
                1, "Жилье", -27500,
                description = "Оплата 16-23 числа"
            ),
        )
    }
}