package com.example.budgettracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * ExpenseEntity represents a single expense entry in the database.
 * It references a CategoryEntity via categoryId (a foreign key relationship).
 */
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            // If the category is deleted, all its expenses get deleted too
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["categoryId"]),  // Makes filtering by category fast
        Index(value = ["date"])          // Makes filtering by date fast
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Double,             // e.g. 150.00
    val description: String,        // e.g. "Grocery shopping at Checkers"
    val date: Long,                 // Unix timestamp — we convert to/from LocalDate
    val categoryId: Int,            // Links to CategoryEntity.id
    val receiptImagePath: String? = null,  // Optional path to a saved photo
    val createdAt: Long = System.currentTimeMillis()
)