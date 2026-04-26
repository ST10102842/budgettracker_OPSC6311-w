package com.example.budgettracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budgettracker.data.model.Category
import com.example.budgettracker.data.model.Expense
import com.example.budgettracker.viewmodel.ExpenseViewModel
import java.time.LocalDate
import kotlin.math.PI

// Slice colors for the pie chart
val CHART_COLORS = listOf(
    Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFF9C27B0),
    Color(0xFFFF9800), Color(0xFFE91E63), Color(0xFF00BCD4),
    Color(0xFFFF5722), Color(0xFF607D8B)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ExpenseViewModel,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToExpenseList: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val monthlyTotal by viewModel.monthlyTotal.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()

    val categorySpendingMap = remember(allExpenses) {
        val now = LocalDate.now()
        val startOfMonth = now.withDayOfMonth(1)
        allExpenses
            .filter { !it.date.isBefore(startOfMonth) && !it.date.isAfter(now) }
            .groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddExpense) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Monthly Total Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("This Month's Spending", fontSize = 13.sp)
                    Text(
                        text = "R ${String.format("%.2f", monthlyTotal)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Spending pie chart (only show if there's data)
            if (categorySpendingMap.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Spending by Category", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        val total = categorySpendingMap.values.sum()

                        // Draw the pie chart using Canvas
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            var startAngle = -90f
                            categorySpendingMap.entries.forEachIndexed { index, entry ->
                                val sweepAngle = ((entry.value / total) * 360f).toFloat()
                                drawArc(
                                    color = CHART_COLORS[index % CHART_COLORS.size],
                                    startAngle = startAngle,
                                    sweepAngle = sweepAngle,
                                    useCenter = true,
                                    size = androidx.compose.ui.geometry.Size(
                                        minOf(size.width, size.height),
                                        minOf(size.width, size.height)
                                    ),
                                    topLeft = androidx.compose.ui.geometry.Offset(
                                        (size.width - minOf(size.width, size.height)) / 2f,
                                        0f
                                    )
                                )
                                startAngle += sweepAngle
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Legend
                        categorySpendingMap.entries.forEachIndexed { index, entry ->
                            val category = allCategories.find { it.id == entry.key }
                            val percentage = (entry.value / total * 100).toInt()
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Canvas(modifier = Modifier.size(14.dp).align(Alignment.CenterVertically)) {
                                        drawCircle(color = CHART_COLORS[index % CHART_COLORS.size])
                                    }
                                    Text("${category?.iconEmoji ?: "📦"} ${category?.name ?: "Unknown"}", fontSize = 13.sp)
                                }
                                Text(
                                    text = "R${String.format("%.0f", entry.value)} ($percentage%)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Navigation Buttons
            OutlinedButton(
                onClick = onNavigateToExpenseList,
                modifier = Modifier.fillMaxWidth()
            ) { Text("View All Expenses") }

            OutlinedButton(
                onClick = onNavigateToCategories,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Manage Categories") }

            Spacer(modifier = Modifier.height(72.dp)) // Space for FAB
        }
    }
}