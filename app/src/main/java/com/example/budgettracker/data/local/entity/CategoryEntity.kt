package com.example.budgettracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,           // e.g. "Food", "Transport"
    val iconEmoji: String,      // e.g. "🍔", "🚗"
    val colorHex: String,       // e.g. "#FF5722"
    val monthlyBudget: Double = 0.0,  // optional budget per category
    val createdAt: Long = System.currentTimeMillis()
)