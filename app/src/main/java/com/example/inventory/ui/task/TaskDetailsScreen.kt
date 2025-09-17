// TaskDetailsScreen.kt
package com.example.inventory.ui.task

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
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

object TaskDetailsDestination : NavigationDestination {
    override val route = "task_details"
    override val titleRes = R.string.task_detail_title
    const val taskIdArg = "taskId"
    val routeWithArgs = "$route/{$taskIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    navigateToEditTask: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(TaskDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditTask(uiState.taskDetails.id) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_task_title),
                )
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        TaskDetailsBody(
            taskDetailsUiState = uiState,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteTask()
                    navigateBack()
                }
            },
            onConfirm = { newName, newPriority ->
                viewModel.updateTask(
                    name = newName,
                    priority = newPriority
                )
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun TaskDetailsBody(
    taskDetailsUiState: TaskDetailsUiState,
    onDelete: () -> Unit,
    onConfirm: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        // 狀態
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        var editable by rememberSaveable { mutableStateOf(false) }
        var inputText by rememberSaveable { mutableStateOf(taskDetailsUiState.taskDetails.name) }
        var selectedPriority by rememberSaveable { mutableStateOf(taskDetailsUiState.taskDetails.priority) }

        // 原始卡片顯示
        TaskDetails(
            task = taskDetailsUiState.taskDetails.toTask(),
            modifier = Modifier.fillMaxWidth()
        )

        // 刪除按鈕
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(stringResource(R.string.delete))
        }

        // Edit / Cancel & Confirm
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!editable) {
                OutlinedButton(onClick = { editable = true }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.edit))
                }
            } else {
                OutlinedButton(onClick = {
                    // rollback
                    editable = false
                    inputText = taskDetailsUiState.taskDetails.name
                    selectedPriority = taskDetailsUiState.taskDetails.priority
                }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.cancel))
                }
                OutlinedButton(onClick = {
                    editable = false
                    onConfirm(inputText, selectedPriority)
                }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.confirm))
                }
            }
        }

        // 編輯輸入框
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text(stringResource(R.string.task)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = editable
        )

        // Priority RadioGroup
        val priorities = listOf("High", "Medium", "Low")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            priorities.forEach { level ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .selectable(
                            selected = (selectedPriority == level),
                            onClick = { if (editable) selectedPriority = level },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = (selectedPriority == level),
                        onClick = null,
                        enabled = editable
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
                    Text(
                        level,
                        color = when (level.lowercase()) {
                            "high" -> MaterialTheme.colorScheme.error
                            "medium" -> Color(0xFFFBC02D)
                            else -> Color(0xFF388E3C)
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 刪除確認對話框
        if (deleteConfirmationRequired) {
            AlertDialog(
                onDismissRequest = { /* no-op */ },
                title = { Text(stringResource(R.string.attention)) },
                text = { Text(stringResource(R.string.delete_question)) },
                confirmButton = {
                    TextButton(onClick = {
                        deleteConfirmationRequired = false
                        onDelete()
                    }) {
                        Text(stringResource(R.string.yes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { deleteConfirmationRequired = false }) {
                        Text(stringResource(R.string.no))
                    }
                }
            )
        }
    }
}

@Composable
fun TaskDetails(
    task: Task,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {
            TaskDetailsRow(R.string.task, task.name)
            TaskDetailsRow(R.string.priority_level_show, task.priority)
        }
    }
}

@Composable
private fun TaskDetailsRow(
    @StringRes labelResID: Int,
    taskDetail: String
) {
    Row(
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text(text = stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = taskDetail, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun TaskDetailsScreenPreview() {
    InventoryTheme {
        TaskDetailsBody(
            taskDetailsUiState = TaskDetailsUiState(
                taskDetails = TaskDetails(1, "示範任務", "Medium")
            ),
            onDelete = { /* no-op */ },
            onConfirm = { _, _ -> /* no-op */ }
        )
    }
}