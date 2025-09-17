package com.example.inventory.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.data.Task
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.InventoryTheme
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    navigateToItemUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            InventoryTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToItemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.task_entry_title))
            }
        },
    ) { innerPadding ->
        HomeBody(
            taskList = homeUiState.taskList,
            onTaskClick = navigateToItemUpdate,
            onDelete = { task ->
                viewModel.delete(task)
                scope.launch {
                    val res = snackbarHostState.showSnackbar(
                        "已刪除 ${task.name}", actionLabel = "復原", duration = SnackbarDuration.Short
                    )
                    if (res == SnackbarResult.ActionPerformed) viewModel.insert(task)
                }
            },
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }
}

@Composable
private fun HomeBody(
    taskList: List<Task>,
    onTaskClick: (Int) -> Unit,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        if (taskList.isEmpty()) {
            Text(
                stringResource(R.string.no_task_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding)
            )
        } else {
            InventoryList(
                taskList = taskList,
                onItemClick = { onTaskClick(it.id) },
                onDelete = onDelete,
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun InventoryList(
    taskList: List<Task>,
    onItemClick: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding) {
        items(taskList, key = { it.id }) { task ->
            DismissibleTask(
                task = task,
                onDelete = onDelete,
                onClick = onItemClick,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DismissibleTask(
    task: Task,
    onDelete: (Task) -> Unit,
    onClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberDismissState(confirmStateChange = { newState: DismissValue ->
        if (newState == DismissValue.DismissedToEnd || newState == DismissValue.DismissedToStart) {
            onDelete(task)
            true
        } else false
    })

    SwipeToDismiss(
        state = dismissState,
        background = { SwipeBackground(dismissState) },
        dismissContent = {
            InventoryItem(task, modifier.clickable { onClick(task) })
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeBackground(state: DismissState) {
    val bgColor by animateColorAsState(
        if (state.targetValue == DismissValue.Default) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.errorContainer
    )
    val alignment = when (state.dismissDirection) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
        else -> Alignment.Center
    }
    Box(
        Modifier.fillMaxWidth().height(64.dp).background(bgColor).padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        Icon(Icons.Default.Delete, contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer)
    }
}

@Composable
private fun InventoryItem(task: Task, modifier: Modifier = Modifier) {
    val cardColor = when (task.priority.lowercase()) {
        "high" -> MaterialTheme.colorScheme.errorContainer
        "medium" -> Color(0xFFFFF9C4)
        "low" -> Color(0xFFC8E6C9)
        else -> MaterialTheme.colorScheme.surface
    }
    Card(
        modifier,
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(task.name, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.in_stock, task.priority), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeBody() {
    InventoryTheme {
        HomeBody(
            listOf(Task(1, "Task1", "High"), Task(2, "Task2", "Medium"), Task(3, "Task3", "Low")),
            onTaskClick = {}, onDelete = {}
        )
    }
}