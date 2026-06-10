package com.example.budgettracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_limits")
data class BudgetLimitEntity(
    @PrimaryKey
    val id: Int = 1, // We only need one row for global limits
    val minLimit: Double,
    val maxLimit: Double
)
