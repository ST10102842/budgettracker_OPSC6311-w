package com.example.budgettracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.budgettracker.ui.screens.*
import com.example.budgettracker.viewmodel.CategoryViewModel
import com.example.budgettracker.viewmodel.ExpenseViewModel

@Composable
fun BudgetNavGraph(
    navController: NavHostController = rememberNavController(),
    expenseViewModel: ExpenseViewModel,
    categoryViewModel: CategoryViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = expenseViewModel,
                onNavigateToAddExpense = { navController.navigate(Screen.AddExpense.route) },
                onNavigateToExpenseList = { navController.navigate(Screen.ExpenseList.route) },
                onNavigateToCategories  = { navController.navigate(Screen.Categories.route) }
            )
        }

        composable(Screen.AddExpense.route) {
            AddExpenseScreen(
                viewModel = expenseViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ExpenseList.route) {
            ExpenseListScreen(
                viewModel = expenseViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Categories.route) {
            CategoryScreen(                          // ← now fully wired
                viewModel = categoryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}