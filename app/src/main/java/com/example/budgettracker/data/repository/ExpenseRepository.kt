package com.example.budgettracker.data.repository

import com.example.budgettracker.data.local.dao.CategorySpending
import com.example.budgettracker.data.local.dao.ExpenseDao
import com.example.budgettracker.data.local.entity.CategoryEntity
import com.example.budgettracker.data.model.Expense
import com.example.budgettracker.data.model.toEntity
import com.example.budgettracker.data.model.toExpense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val categoryRepository: CategoryRepository
) {

    // Convert LocalDate → milliseconds for Room queries
    private fun LocalDate.toMillis(): Long =
        atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val allExpenses: Flow<List<Expense>> = expenseDao
        .getAllExpenses()
        .map { entities ->
            entities.map { entity ->
                val category = categoryRepository.getCategoryById(entity.categoryId)
                entity.toExpense(category?.let {
                    com.example.budgettracker.data.local.entity.CategoryEntity(
                        id = it.id,
                        name = it.name,
                        iconEmoji = it.iconEmoji,
                        colorHex = it.colorHex,
                        monthlyBudget = it.monthlyBudget
                    )
                })
            }
        }

    fun getExpensesByDateRange(start: LocalDate, end: LocalDate): Flow<List<Expense>> {
        return expenseDao
            .getExpensesByDateRange(start.toMillis(), end.toMillis())
            .map { entities -> entities.map { it.toExpense() } }
    }

    fun getTotalSpendingInRange(start: LocalDate, end: LocalDate): Flow<Double?> {
        return expenseDao.getTotalSpendingInRange(start.toMillis(), end.toMillis())
    }

    fun getSpendingByCategory(start: LocalDate, end: LocalDate): Flow<List<CategorySpending>> {
        return expenseDao.getSpendingByCategory(start.toMillis(), end.toMillis())
    }

    suspend fun insertExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense.toEntity())
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense.toEntity())
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense.toEntity())
    }

    suspend fun deleteExpenseById(id: Int) {
        expenseDao.deleteExpenseById(id)
    }
}