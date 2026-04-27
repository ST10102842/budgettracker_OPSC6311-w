package com.example.budgettracker.data.model

import com.example.budgettracker.data.local.entity.CategoryEntity
import com.example.budgettracker.data.local.entity.ExpenseEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// Convert database entity → UI model
fun ExpenseEntity.toExpense(category: CategoryEntity? = null): Expense {
    return Expense(
        id = id,
        amount = amount,
        description = description,
        date = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate(),
        categoryId = categoryId,
        categoryName = category?.name ?: "",
        categoryEmoji = category?.iconEmoji ?: "",
        categoryColor = category?.colorHex ?: "",
        receiptImagePath = receiptImagePath
    )
}

// Convert UI model → database entity
fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amount = amount,
        description = description,
        date = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        categoryId = categoryId,
        receiptImagePath = receiptImagePath
    )
}

fun CategoryEntity.toCategory() = Category(
    id = id,
    name = name,
    iconEmoji = iconEmoji,
    colorHex = colorHex,
    monthlyBudget = monthlyBudget
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    iconEmoji = iconEmoji,
    colorHex = colorHex,
    monthlyBudget = monthlyBudget
)