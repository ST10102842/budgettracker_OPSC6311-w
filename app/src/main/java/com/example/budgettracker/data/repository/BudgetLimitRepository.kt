package com.example.budgettracker.data.repository

import com.example.budgettracker.data.local.dao.BudgetLimitDao
import com.example.budgettracker.data.model.BudgetLimit
import com.example.budgettracker.data.model.toBudgetLimit
import com.example.budgettracker.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetLimitRepository(private val budgetLimitDao: BudgetLimitDao) {
    val budgetLimit: Flow<BudgetLimit?> = budgetLimitDao
        .getBudgetLimit()
        .map { it?.toBudgetLimit() }

    suspend fun saveBudgetLimit(limit: BudgetLimit) {
        budgetLimitDao.saveBudgetLimit(limit.toEntity())
    }
}
