package com.example.budgettracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgettracker.data.local.database.BudgetDatabase
import com.example.budgettracker.data.repository.CategoryRepository
import com.example.budgettracker.data.repository.ExpenseRepository
import com.example.budgettracker.navigation.BudgetNavGraph
import com.example.budgettracker.ui.theme.BudgetTrackerTheme
import com.example.budgettracker.viewmodel.CategoryViewModel
import com.example.budgettracker.viewmodel.CategoryViewModelFactory
import com.example.budgettracker.viewmodel.ExpenseViewModel
import com.example.budgettracker.viewmodel.ExpenseViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = BudgetDatabase.getDatabase(applicationContext)
        val categoryRepository = CategoryRepository(database.categoryDao())
        val expenseRepository = ExpenseRepository(database.expenseDao(), categoryRepository)

        val expenseFactory  = ExpenseViewModelFactory(expenseRepository, categoryRepository)
        val categoryFactory = CategoryViewModelFactory(categoryRepository)

        setContent {
            BudgetTrackerTheme {
                val expenseViewModel: ExpenseViewModel = viewModel(factory = expenseFactory)
                val categoryViewModel: CategoryViewModel = viewModel(factory = categoryFactory)

                BudgetNavGraph(
                    expenseViewModel = expenseViewModel,
                    categoryViewModel = categoryViewModel
                )
            }
        }
    }
}