package com.example.budgettracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.budgettracker.data.model.Category

// Preset emoji choices for categories
private val EMOJI_OPTIONS = listOf(
    "🍔","🍕","☕","🛒","🚗","🚌","✈️","🏠","💡","📱",
    "🎮","🎬","🎵","📚","💊","🏋️","👗","💇","🐾","🎁",
    "💰","📦","🏦","🧾","🌍","⚽","🎯","🛠️","🧴","🍷"
)

// Preset color choices
private val COLOR_OPTIONS = listOf(
    "#F44336","#E91E63","#9C27B0","#673AB7",
    "#3F51B5","#2196F3","#03A9F4","#00BCD4",
    "#009688","#4CAF50","#8BC34A","#CDDC39",
    "#FFC107","#FF9800","#FF5722","#607D8B"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategorySheet(
    categoryToEdit: Category?,        // null = add mode, non-null = edit mode
    onDismiss: () -> Unit,
    onSave: (name: String, emoji: String, colorHex: String, budget: Double) -> Unit,
    onUpdate: (Category) -> Unit
) {
    val isEditMode = categoryToEdit != null

    // Pre-fill fields if editing
    var name by remember(categoryToEdit) {
        mutableStateOf(categoryToEdit?.name ?: "")
    }
    var selectedEmoji by remember(categoryToEdit) {
        mutableStateOf(categoryToEdit?.iconEmoji ?: "📦")
    }
    var selectedColor by remember(categoryToEdit) {
        mutableStateOf(categoryToEdit?.colorHex ?: "#607D8B")
    }
    var budgetText by remember(categoryToEdit) {
        mutableStateOf(
            if ((categoryToEdit?.monthlyBudget ?: 0.0) > 0)
                categoryToEdit!!.monthlyBudget.toInt().toString()
            else ""
        )
    }
    var nameError by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Sheet Title
            Text(
                text = if (isEditMode) "Edit Category" else "New Category",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // ── Name Field ──────────────────────────────────
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = null
                },
                label = { Text("Category name") },
                placeholder = { Text("e.g. Groceries") },
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ── Monthly Budget Field ────────────────────────
            OutlinedTextField(
                value = budgetText,
                onValueChange = { budgetText = it },
                label = { Text("Monthly budget (R) — optional") },
                placeholder = { Text("Leave blank for no limit") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ── Emoji Picker ────────────────────────────────
            Text("Icon", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 160.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(EMOJI_OPTIONS) { emoji ->
                    val isSelected = emoji == selectedEmoji
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedEmoji = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 20.sp)
                    }
                }
            }

            // ── Color Picker ────────────────────────────────
            Text("Color", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            LazyVerticalGrid(
                columns = GridCells.Fixed(8),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(COLOR_OPTIONS) { colorHex ->
                    val isSelected = colorHex == selectedColor
                    val color = runCatching {
                        Color(android.graphics.Color.parseColor(colorHex))
                    }.getOrDefault(Color.Gray)

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = colorHex }
                    )
                }
            }

            // ── Preview ─────────────────────────────────────
            val previewColor = runCatching {
                Color(android.graphics.Color.parseColor(selectedColor))
            }.getOrDefault(Color.Gray)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(previewColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(selectedEmoji, fontSize = 20.sp)
                }
                Column {
                    Text(
                        text = name.ifBlank { "Category name" },
                        fontWeight = FontWeight.SemiBold,
                        color = if (name.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (budgetText.isNotBlank()) "Budget: R$budgetText/month"
                        else "No budget set",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Save Button ─────────────────────────────────
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = "Please enter a category name"
                        return@Button
                    }
                    val budget = budgetText.toDoubleOrNull() ?: 0.0

                    if (isEditMode) {
                        onUpdate(
                            categoryToEdit!!.copy(
                                name = name.trim(),
                                iconEmoji = selectedEmoji,
                                colorHex = selectedColor,
                                monthlyBudget = budget
                            )
                        )
                    } else {
                        onSave(name.trim(), selectedEmoji, selectedColor, budget)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (isEditMode) "Save Changes" else "Add Category",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}