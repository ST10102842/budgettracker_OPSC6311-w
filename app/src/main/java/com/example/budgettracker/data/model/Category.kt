package com.example.budgettracker.data.model

data class Category(
    val id: Int = 0,
    val name: String,
    val iconEmoji: String,
    val colorHex: String,
    val monthlyBudget: Double = 0.0
)