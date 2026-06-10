package com.example.budgettracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.budgettracker.data.local.entity.BudgetLimitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetLimitDao {
    @Query("SELECT * FROM budget_limits WHERE id = 1")
    fun getBudgetLimit(): Flow<BudgetLimitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBudgetLimit(limit: BudgetLimitEntity)
}
