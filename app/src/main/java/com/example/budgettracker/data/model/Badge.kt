package com.example.budgettracker.data.model

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val isUnlocked: Boolean = false
)