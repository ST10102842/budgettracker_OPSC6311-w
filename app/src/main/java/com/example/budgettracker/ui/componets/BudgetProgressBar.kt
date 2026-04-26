package com.example.budgettracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BudgetProgressBar(
    categoryName: String,
    emoji: String,
    spent: Double,
    budget: Double
) {
    if (budget <= 0) return  // Don't show if no budget set

    val progress = (spent / budget).coerceIn(0.0, 1.0).toFloat()
    val isOverBudget = spent > budget

    val progressColor = when {
        progress >= 1.0f -> Color(0xFFE53935)   // Red: over budget
        progress >= 0.8f -> Color(0xFFFF9800)   // Orange: near limit
        else             -> Color(0xFF4CAF50)   // Green: healthy
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$emoji $categoryName",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = "R${String.format("%.0f", spent)} / R${String.format("%.0f", budget)}",
                fontSize = 13.sp,
                color = if (isOverBudget) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = progressColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        if (isOverBudget) {
            Text(
                text = "Over budget by R${String.format("%.2f", spent - budget)}",
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}