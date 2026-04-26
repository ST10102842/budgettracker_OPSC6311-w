package com.example.budgettracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budgettracker.data.model.Expense
import com.example.budgettracker.viewmodel.ExpenseViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    viewModel: ExpenseViewModel,
    onNavigateBack: () -> Unit
) {
    val filteredExpenses by viewModel.filteredExpenses.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()
    val selectedDateRange by viewModel.selectedDateRange.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()

    var showFilters by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    // Delete confirmation dialog
    expenseToDelete?.let { expense ->
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { Text("Delete Expense?") },
            text = { Text("This will permanently remove \"${expense.description}\"") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteExpense(expense.id)
                    expenseToDelete = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Expenses", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = if (showFilters) Icons.Default.Close else Icons.Default.FilterList,
                            contentDescription = "Toggle Filters"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Filter Panel
            if (showFilters) {
                FilterPanel(
                    categories = categories,
                    selectedDateRange = selectedDateRange,
                    selectedCategoryId = selectedCategoryId,
                    onDateRangeChange = { start, end -> viewModel.setDateRange(start, end) },
                    onCategoryChange = { viewModel.setCategoryFilter(it) },
                    onClearFilters = { viewModel.clearAllFilters() }
                )
            }

            // Expenses List
            if (filteredExpenses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💸", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No expenses found", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        if (selectedDateRange != null || selectedCategoryId != null) {
                            Text("Try adjusting your filters", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Text("Tap + Add Expense to get started", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredExpenses, key = { it.id }) { expense ->
                        ExpenseListItem(
                            expense = expense,
                            onDeleteClick = { expenseToDelete = expense }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseListItem(expense: Expense, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = expense.categoryEmoji.ifEmpty { "📦" }, fontSize = 28.sp)
                Column {
                    Text(expense.description, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text(
                        text = "${expense.categoryName} • ${expense.date}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "R ${String.format("%.2f", expense.amount)}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun FilterPanel(
    categories: List<com.example.budgettracker.data.model.Category>,
    selectedDateRange: Pair<LocalDate, LocalDate>?,
    selectedCategoryId: Int?,
    onDateRangeChange: (LocalDate, LocalDate) -> Unit,
    onCategoryChange: (Int?) -> Unit,
    onClearFilters: () -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(selectedDateRange?.first ?: LocalDate.now()) }
    var endDate by remember { mutableStateOf(selectedDateRange?.second ?: LocalDate.now()) }

    // Start Date Picker
    if (showStartDatePicker) {
        DatePickerModal(
            onDateSelected = { dateMillis ->
                if (dateMillis != null) {
                    startDate = LocalDate.ofEpochDay(dateMillis / (24 * 60 * 60 * 1000))
                }
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    // End Date Picker
    if (showEndDatePicker) {
        DatePickerModal(
            onDateSelected = { dateMillis ->
                if (dateMillis != null) {
                    endDate = LocalDate.ofEpochDay(dateMillis / (24 * 60 * 60 * 1000))
                }
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date Range Filter
            Text("Date Range", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Start: ${startDate}", fontSize = 11.sp)
                }
                Button(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("End: ${endDate}", fontSize = 11.sp)
                }
            }
            Button(
                onClick = {
                    onDateRangeChange(startDate, endDate)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Date Range")
            }

            Divider()

            // Category Filter
            Text("Category", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            categories.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { category ->
                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = {
                                onCategoryChange(
                                    if (selectedCategoryId == category.id) null else category.id
                                )
                            },
                            label = { Text("${category.iconEmoji} ${category.name}", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Add spacer for odd count
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Divider()

            // Clear Filters Button
            if (selectedDateRange != null || selectedCategoryId != null) {
                Button(
                    onClick = {
                        onClearFilters()
                        startDate = LocalDate.now()
                        endDate = LocalDate.now()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear All Filters")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

