package com.example.budgettracker.data.model

import java.time.LocalDate


data class Expense(
    val id: Int = 0,
    val amount: Double,
    val description: String,
    val date: LocalDate,
    val categoryId: Int,
    val categoryName: String = "",
    val categoryEmoji: String = "",
    val categoryColor: String = "",
    val receiptImagePath: String? = null
)